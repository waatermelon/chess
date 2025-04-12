package server;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.CommandExtension;
import websocket.commands.UserGameCommand;
import websocket.messages.MessageExtension;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    Gson serializer = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        CommandExtension data = serializer.fromJson(message, CommandExtension.class);
        switch (data.getCommandType()) {
            case UserGameCommand.CommandType.CONNECT -> playerJoin(session, data);
            case UserGameCommand.CommandType.LEAVE -> leave(session, data);
            case UserGameCommand.CommandType.RESIGN -> resign(session, data);
            case UserGameCommand.CommandType.MAKE_MOVE -> move(session, data);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {}

    private void playerJoin(Session session, CommandExtension data) {
        AuthData authData;
        GameData game;
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
        } catch (Exception e) {
            clientError(session, "Please try joining as a different color or a different game.");
            return;
        }

        if (data.getTeamColor() != null) {
            Server.sessions.put(session, data.getGameID());
            MessageExtension message = new MessageExtension(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    data.getUsername() + " joined the game as " +
                            data.getTeamColor().toString().toLowerCase() + "!"
            );

            try {
                sendMessagetoGame(session, message, false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            Server.sessions.put(session, data.getGameID());
            MessageExtension message = new MessageExtension(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    data.getUsername() + " joined the game as a viewer!"
            );
            try {
                sendMessagetoGame(session, message, false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        MessageExtension loadGameMessage = new MessageExtension(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
        );
        try {
            sendMessage(session, loadGameMessage);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void leave(Session session, CommandExtension data) {
        AuthData authData;
        GameData game;
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
            String username = authData.username();
            if (Objects.equals(game.whiteUsername(), username)) {
                GameData newGame = new GameData(
                        game.gameID(), null, game.blackUsername(),
                        game.gameName(), game.game()
                );

                Server.gameDAO.updateGame(newGame);
            } else if (Objects.equals(game.blackUsername(), username)) {
                GameData newGame = new GameData(
                        game.gameID(), game.whiteUsername(), null,
                        game.gameName(), game.game()
                );

                Server.gameDAO.updateGame(newGame);
            }
        } catch (DataAccessException e) {
            System.out.println("Unable to leave, please type \"help\" for a list of commands.");
        }

        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.NOTIFICATION,
                data.getUsername() + " has left the game."
        );
        try {
            sendMessagetoGame(session, message, false);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Server.sessions.put(session, -1);
    }

    private void resign(Session session, CommandExtension data) {
        AuthData authData;
        GameData game;
        ChessGame.TeamColor color;
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
            String username = authData.username();

            if (game.game().getGameFinished()) {
                throw new ArithmeticException();
            }
            if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
                throw new RuntimeException();
            }
            game.game().setGameFinished(true);
            Server.gameDAO.updateGame(game);
        } catch (ArithmeticException e) {
            clientError(session, "Game has already finished.");
            return;
        } catch (RuntimeException e) {
            clientError(session, "Viewers are unable to resign. Use \"leave\" to exit match.");
            return;
        } catch (DataAccessException e) {
            clientError(session, "Game is not able to be resigned from.");
            return;
        }


        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.NOTIFICATION,
                data.getUsername() + " has resigned."
        );
        try {
            sendMessagetoGame(session, message, true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void move(Session session, CommandExtension data) {
        AuthData authData;
        GameData game;

        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
            String username = authData.username();
            if (game.game().getGameFinished()) {
                throw new RuntimeException();
            }
            if (game.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
                if (!Objects.equals(game.whiteUsername(), username)) {
                    System.out.println("white user does not match");
                    throw new RuntimeException();
                }
                System.out.println("white user did match");
            } else {
                if (!Objects.equals(game.blackUsername(), username)) {
                    System.out.println("black user does not match");
                    throw new RuntimeException();
                }
                System.out.println("black user did match");
            }


            game.game().makeMove(data.getChessMove());
            Server.gameDAO.updateGame(game);
        } catch (RuntimeException e) {
            clientError(session, "It is currently your opponents turn.");
            return;
        } catch (InvalidMoveException e) {
            clientError(session, "Illegal move, please enter "+
                    "\"preview\" to see possible moves.");
            return;
        } catch (DataAccessException e) {
            clientError(session, "Move disallowed");
            return;
        }

        ChessPosition startPosition = data.getChessMove().getStartPosition();
        ChessPosition endPosition = data.getChessMove().getEndPosition();
        String startPos = posToChessPos(startPosition);
        String endPos = posToChessPos(endPosition);

        MessageExtension loadGameMessage = new MessageExtension(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
        );
        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.NOTIFICATION,
                data.getUsername() + " moved " + startPos + " to " + endPos + "."
        );

        try {
            sendMessagetoGame(session, loadGameMessage, true);
            sendMessagetoGame(session, message, false);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String posToChessPos(ChessPosition pos) {
        char file = (char) ('a' + pos.getColumn() - 1);
        String rank = String.valueOf(pos.getRow());
        return file + rank;
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }

    private void sendMessagetoGame(Session session, ServerMessage message, boolean all)
            throws IOException {

        for (Session serverSession : Server.sessions.keySet()) {
            if (Server.sessions.get(serverSession) == -1) {
                continue;
            }
            if (!Objects.equals(Server.sessions.get(serverSession), Server.sessions.get(session))) {
                continue;
            }
            if (serverSession == session) {
                if (!all) {
                    continue;
                }
            }

            sendMessage(serverSession, message);
        }
    }

    private void clientError(Session session, String errorMessage){
        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.ERROR, null, "Error: " + errorMessage
        );
        try {
            sendMessage(session, message);
        } catch ( Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
