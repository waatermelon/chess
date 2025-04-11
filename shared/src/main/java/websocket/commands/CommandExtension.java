package websocket.commands;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public class CommandExtension extends UserGameCommand{

    TeamColor color;
    ChessMove move;

    public CommandExtension
            (CommandType type, String authToken, int gameID, TeamColor color, ChessMove move) {
        super(type, authToken, gameID);
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
