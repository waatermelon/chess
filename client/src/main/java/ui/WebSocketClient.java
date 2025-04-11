package ui;

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

    public WebSocketClient(String port) {
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);


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

        printMessage(data.getMessage());
        // TODO use GSON convert to java-class for interpretation
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
        System.out.print("\n" + message + "\n[LOGGED_IN] >>> ");

    }
}
