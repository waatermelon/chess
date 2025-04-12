package websocket.messages;

import model.GameData;

public class MessageExtension extends ServerMessage{

    String message;
    String errorMessage;
    boolean lastMessage;
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

    public MessageExtension(ServerMessageType type, GameData game, boolean lastMessage) {
        super(type);
        this.game = game;
        this.lastMessage = lastMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public GameData getGameData() {
        return game;
    }

    public boolean getLastMessage() {
        return lastMessage;
    }
}
