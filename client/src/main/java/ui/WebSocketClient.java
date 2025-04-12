package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.MessageExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.*;



@ClientEndpoint
public class WebSocketClient extends Endpoint {

    Gson serializer = new Gson();
    private Session session;
    private ChessLoop loop;

    public WebSocketClient(String port, ChessLoop loop) {
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);

            this.loop = loop;

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                clientMessaged(message);
            }
        });
    }

    public void closeSession() throws IOException {
        if (this.session != null) {
            this.session.close();
        }
    }

    private void clientMessaged(String message) {
        MessageExtension data = serializer.fromJson(message, MessageExtension.class);
        switch(data.getServerMessageType()) {
            case LOAD_GAME -> printLoad(data);
            case NOTIFICATION -> printMessage(data.getMessage());
            case ERROR -> printError(data.getErrorMessage());
        }
    }

    private void printLoad(MessageExtension data) {
        loop.currentGame = data.getGameData();

        BoardPrinter boardPrinter = new BoardPrinter();
        System.out.println();
        boardPrinter.printBoard(loop.currentGame, loop.currentColor);
        if (data.getLastMessage()) {
            System.out.print("[LOGGED IN] >>> ");
        }
    }

    private void printError(String errorMessage) {
        System.out.print("\n" + errorMessage + "\n[LOGGED IN] >>> ");
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("unable to send message.");
        }
    }

    private void printMessage(String message) {
        if (message.isEmpty()) {
            return;
        }
        System.out.print("\n" + message + "\n[LOGGED_IN] >>> ");

    }
}
