package server;

import chess.ChessGame;
import com.google.gson.Gson;
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

    // Business logic methods for future expansion
    private void playerJoin(Session session, CommandExtension data) {
        AuthData authData;
        GameData game;
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
        } catch (Exception e) {
            clientError(session);
            return;
        }

        if (data.getTeamColor() != null) {
            // player

            System.out.println("player joined game as player");
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
            // viewer
            System.out.println("player joined game as viewer!");
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
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
        } catch (Exception e) {
            clientError(session);
            return;
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
        //TODO Implementation for resign logic
        AuthData authData;
        GameData game;
        ChessGame.TeamColor color;
        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
            color = data.getTeamColor();
            if (color == null) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            clientError(session);
            return;
        }

        Server.sessions.put(session, -1);
        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.NOTIFICATION,
                data.getUsername() + " has resigned."
        );
        try {
            sendMessagetoGame(session, message, false);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void move(Session session, CommandExtension data) {
        //TODO Implementation for processing a move
        AuthData authData;
        GameData game;
        ChessGame.TeamColor color;

        try {
            authData = Server.authDAO.getAuth(data.getAuthToken());
            game = Server.gameDAO.getGame(data.getGameID());
            color = data.getTeamColor();
        } catch (Exception e) {
            clientError(session);
            return;
        }

        String startPos = String.valueOf(data.getChessMove().getStartPosition().getRow());
        startPos += " " + String.valueOf(data.getChessMove().getStartPosition().getColumn());

        String endPos = String.valueOf(data.getChessMove().getEndPosition().getRow());
        endPos += " " + String.valueOf(data.getChessMove().getEndPosition().getColumn());
        MessageExtension loadGameMessage = new MessageExtension(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
        );
        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.NOTIFICATION,
                data.getUsername() + " moved " + startPos + " to " + endPos + "."
        );

        try {
            System.out.println("BEFORE SENDS");
            sendMessagetoGame(session, loadGameMessage, true);
            sendMessagetoGame(session, message, false);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }

    private void sendMessagetoGame(Session session, ServerMessage message, boolean all)
            throws IOException {

        for (Session serverSession : Server.sessions.keySet()) {
            System.out.println("server session: " + Server.sessions.get(serverSession) + " : " + Server.sessions.get(session));
            if (Server.sessions.get(serverSession) == -1) {
                continue;
            }
            if (!Objects.equals(Server.sessions.get(serverSession), Server.sessions.get(session))) {
                continue;
            }
            if (serverSession == session) {
                if (!all) {
                    System.out.println("nah");
                    continue;
                }
            }
            System.out.println("passed all checks, sending");
            sendMessage(serverSession, message);
        }
    }

    private void clientError(Session session){
        MessageExtension message = new MessageExtension(
                ServerMessage.ServerMessageType.ERROR, null, "Error"
        );
        try {
            sendMessage(session, message);
        } catch ( Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
