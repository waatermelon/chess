package websocket.messages;

import model.GameData;

public class MessageExtension extends ServerMessage{

    String message;
    String errorMessage;
    GameData game;

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

    public MessageExtension(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
