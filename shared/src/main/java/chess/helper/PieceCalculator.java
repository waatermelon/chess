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


    /***
     *
     * @param position  The current position of the piece.
     * @param row       The row(y/i) movement. (-1, 0, 1)
     * @param col       The column(x/j) movement. (-1, 0, 1)
     * @return          The possible moves based off of the given row and column directions.
     */
    private Collection<ChessMove> directionalMove(ChessPosition position, int row, int col) {
        ArrayList<int[]> moves = new ArrayList<>();

        for(int i = position.getRow() + row; row != 0 && i < ((row > 0) ? 8 : -1); i += row)
            moves.add(new int[]{0, i});

        for(int j = position.getColumn() + col, k = 0, l = 0; col != 0 && j < ((col > 0) ? 8 : -1); j += col) {
            ++l;
            if(moves.isEmpty()) k = 1;
            if(k == 1)
                moves.add(new int[]{j, 0});
            else
                moves.get(l)[0] = j;
        }

        ArrayList<ChessMove> movesList = new ArrayList<>();
        for (int[] move : moves)
            movesList.add(new ChessMove(position, new ChessPosition(move[1], move[0]), type));

        return movesList;
    }

    private Collection<ChessMove> pawnMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int yChange = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        moves.add(new ChessMove(position, new ChessPosition(position.getRow() + yChange, position.getColumn()), type));
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int i = 0; i < 4; ++i) {
            for(int j = -1; j < 1; ++j) {

            }
        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(directionalMove(position, 0, -1));
        moves.addAll(directionalMove(position, 0, 1));
        moves.addAll(directionalMove(position, -1, 0));
        moves.addAll(directionalMove(position, -1, 0));

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(directionalMove(position, 1, -1));
        moves.addAll(directionalMove(position, 1, 1));
        moves.addAll(directionalMove(position, -1, -1));
        moves.addAll(directionalMove(position, -1, 1));

        return moves;
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
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(bishopMoves(position));
        moves.addAll(rookMoves(position));

        return moves;
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
