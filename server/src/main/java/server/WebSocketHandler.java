package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.CommandExtension;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


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
            case UserGameCommand.CommandType.CONNECT -> playerJoin(data);
            case UserGameCommand.CommandType.LEAVE -> leave(data);
            case UserGameCommand.CommandType.RESIGN -> resign(data);
            case UserGameCommand.CommandType.MAKE_MOVE -> move(data);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {}

    // Business logic methods for future expansion
    private void playerJoin(CommandExtension data) {
        //TODO Implementation for player joining logic
        //TODO if player not null then joining as player
        System.out.println(data);
        if (data.getTeamColor() != null) {
            // player
            System.out.println("player joined game as player");

        } else {
            // viewer
            System.out.println("player joined game as VIEWER!");
        }
    }

    private void leave(CommandExtension data) {
        //TODO Implementation for leave logic
    }

    private void resign(CommandExtension data) {
        //TODO Implementation for resign logic
    }

    private void move(CommandExtension data) {
        //TODO Implementation for processing a move
    }



    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }
}
