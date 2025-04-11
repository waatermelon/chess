package ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.*;



@ClientEndpoint
public class WebSocketClient extends Endpoint {

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
        System.out.println("opened session successfully!"); //TODO TESTING
        this.session = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                clientMessaged(message);
            }
        });
    }

    private void clientMessaged(String message) {
        System.out.println("received message successfully!"); //TODO TESTING
        System.out.println(message);

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
}
