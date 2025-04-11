package server;

import com.google.gson.Gson;
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
        System.out.println("Connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed: " + session + ", code: " + statusCode + ", reason: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received message from " + session + ": " + message);
        CommandExtension data = serializer.fromJson(message, CommandExtension.class);
        switch (data.getCommandType()) {
            case UserGameCommand.CommandType.CONNECT -> playerJoin(session, data);
            case UserGameCommand.CommandType.LEAVE -> leave(session, data);
            case UserGameCommand.CommandType.RESIGN -> resign(session, data);
            case UserGameCommand.CommandType.MAKE_MOVE -> move(session, data);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        Server.sessions.put(session, -1);
    }

    // Business logic methods for future expansion
    private void playerJoin(Session session, CommandExtension data) {
        //TODO Implementation for player joining logic
        //TODO if player not null then joining as player
        System.out.println(data);
        if (data.getTeamColor() != null) {
            // player
            System.out.println("player joined game as player");
            Server.sessions.put(session, data.getGameID());
            MessageExtension message = new MessageExtension(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "whut"
            );
            try {
                sendMessagetoGame(session, message);
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            // viewer
            System.out.println("player joined game as VIEWER!");
            Server.sessions.put(session, data.getGameID());

        }
    }

    private void leave(Session session, CommandExtension data) {
        //TODO Implementation for leave logic
    }

    private void resign(Session session, CommandExtension data) {
        //TODO Implementation for resign logic
    }

    private void move(Session session, CommandExtension data) {
        //TODO Implementation for processing a move
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }

    private void sendMessagetoGame(Session session, ServerMessage message) throws IOException {

        for (Session serverSession : Server.sessions.keySet()) {
            if (Server.sessions.get(serverSession) == -1) {
                continue;
            }
            if (!Objects.equals(Server.sessions.get(serverSession), Server.sessions.get(session))) {
                continue;
            }
            if (serverSession == session) {
                continue;
            }
            // end guard clause
            System.out.println("sent message to a player!");
            sendMessage(session, message);
        }
    }
}
