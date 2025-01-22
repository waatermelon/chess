package chess.helper;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PieceCalculator {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor teamColor;

    public PieceCalculator(ChessPiece.PieceType type, ChessGame.TeamColor teamColor) {
        this.type = type;
        this.teamColor = teamColor;
    }

    private Collection<ChessMove> pawnMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int yChange = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        moves.add(new ChessMove(position, new ChessPosition(position.getRow() + yChange, position.getColumn()), type));
        return moves;
    }
    private Collection<ChessMove> knightMoves(ChessPosition position) {

    }
    private Collection<ChessMove> rookMoves(ChessPosition position) {

    }
    private Collection<ChessMove> bishopMoves(ChessPosition position) {

    }
    private Collection<ChessMove> kingMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int i = -1; i < 2; ++i) {
            for(int j = -1; j < 2; ++j) {
                if(i == 0 && j == 0) continue;
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + i, position.getColumn() + j), type));
            }
        }
        return moves;
    }
    private Collection<ChessMove> queenMoves(ChessPosition position) {

    }

    public static Collection<ChessMove> possibleMoves(ChessPiece.PieceType type, ChessGame.TeamColor teamColor, ChessPosition position) {
        PieceCalculator pc = new PieceCalculator(type, teamColor);
        return pc.possibleMoves(position);
    }

    public Collection<ChessMove> possibleMoves(ChessPosition position) {
        return switch (this.type) {
            case PAWN -> pawnMoves(position);
            case ROOK -> rookMoves(position);
            case BISHOP -> bishopMoves(position);
            case KNIGHT -> knightMoves(position);
            case KING -> kingMoves(position);
            case QUEEN -> queenMoves(position);
            default -> null;
        };
    }
}
