package websocket.messages;

public class MessageExtension extends ServerMessage{

    String message;

    public MessageExtension(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
