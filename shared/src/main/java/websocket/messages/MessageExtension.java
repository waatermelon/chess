package websocket.messages;

public class MessageExtension extends ServerMessage{

    String message;
    String errorMessage;

    public MessageExtension(ServerMessageType type, String message) {
        super(type);
        this.message = message;
        this.errorMessage = null;
    }

    public MessageExtension(ServerMessageType type, String message, String errorMessage) {
        super(type);
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
