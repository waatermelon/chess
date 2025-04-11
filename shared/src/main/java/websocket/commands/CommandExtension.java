package websocket.commands;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public class CommandExtension extends UserGameCommand{

    String username;
    TeamColor color;
    ChessMove move;

    public CommandExtension(
            CommandType type, String authToken, int gameID,
            String username, TeamColor color, ChessMove move)
    {
        super(type, authToken, gameID);
        this.username = username;
        this.color = color;
        this.move = move;
    }

    public TeamColor getTeamColor() {
        return color;
    }

    public ChessMove getChessMove() {
        return move;
    }

    @Override
    public String toString() {
        return "CommandExtension{" +
                "color=" + color +
                ", move=" + move +
                '}';
    }
}
