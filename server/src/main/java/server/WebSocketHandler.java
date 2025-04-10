package server;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void  connect() {

    }

    @OnWebSocketClose
    public void close() {

    }

    @OnWebSocketMessage
    public void message() {

    }

    private void playerJoin() {

    }

    private void viewerJoin() {

    }

    private void leave() {

    }

    private void resign() {

    }

    private void move() {

    }

}
