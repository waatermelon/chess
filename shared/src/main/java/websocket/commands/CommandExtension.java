package websocket.commands;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public class CommandExtension extends UserGameCommand{

    String username;
    TeamColor playerColor;
    ChessMove move;

    public CommandExtension(
            CommandType type, String authToken, int gameID,
            String username, TeamColor playerColor, ChessMove move)
    {
        super(type, authToken, gameID);
        this.username = username;
        this.playerColor = playerColor;
        this.move = move;
    }

    public TeamColor getTeamColor() {
        return playerColor;
    }

    public ChessMove getChessMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "CommandExtension{" +
                "color=" + playerColor +
                ", move=" + move +
                '}';
    }
}
