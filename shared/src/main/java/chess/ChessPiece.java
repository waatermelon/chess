package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves =  new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()) {
            case KING -> kingMoves(board, myPosition, moves);
            case QUEEN -> queenMoves(board, myPosition, moves);
            case BISHOP -> bishopMoves(board, myPosition, moves);
            case ROOK -> rookMoves(board, myPosition, moves);
            case KNIGHT -> knightMoves(board, myPosition, moves);
            case PAWN -> pawnMoves(board, myPosition, moves);
        }
        return moves;
    }

    private void kingMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] instanceMove = new int[][] {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1},};
        r(instanceMove, board, myPosition, moves, false);
    }

    private void queenMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] instanceMove = new int[][] {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1},};
        r(instanceMove, board, myPosition, moves, true);
    }

    private void rookMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] instanceMove = new int[][] {{0,-1},{0,1},{-1,0},{1,0},};
        r(instanceMove, board, myPosition, moves, true);
    }

    private void bishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] instanceMove = new int[][] {{-1,-1},{1,-1},{-1,1},{1,1},};
        r(instanceMove, board, myPosition, moves, true);
    }

    private void knightMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] instanceMove = new int[][] {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2},};
        r(instanceMove, board, myPosition, moves, false);
    }

    private void pawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int pawnDir = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean doubleJmp = pawnDir > 0 ? myPosition.getRow() == 2 : myPosition.getRow() == 7;

        if(acceptablePosPawn(board, new ChessPosition(myPosition.getRow() + pawnDir,
                myPosition.getColumn() - 1), getTeamColor(), true)){
            promoteMove(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + pawnDir,
                    myPosition.getColumn() - 1), null), moves);
        }
        if(acceptablePosPawn(board, new ChessPosition(myPosition.getRow() + pawnDir,
                myPosition.getColumn() + 1), getTeamColor(), true)){
            promoteMove(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + pawnDir,
                    myPosition.getColumn() + 1), null), moves);
        }
        int currentLen = moves.size();
        if(acceptablePosPawn(board, new ChessPosition(myPosition.getRow() + pawnDir,
                myPosition.getColumn()), getTeamColor(), false)){
            promoteMove(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + pawnDir,
                    myPosition.getColumn()), null), moves);
        }
        if(moves.size() > currentLen && doubleJmp && acceptablePosPawn(board,
                new ChessPosition(myPosition.getRow() + (pawnDir * 2),
                        myPosition.getColumn()), getTeamColor(), false)){
            promoteMove(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +
                    (pawnDir * 2), myPosition.getColumn()), null), moves);
        }
    }

    private void rr(int[] imove, ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        if(acceptablePos(board, new ChessPosition(myPosition.getRow() + imove[0],
                myPosition.getColumn() + imove[1]), board.getPiece(myPosition).getTeamColor())) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + imove[0],
                    myPosition.getColumn() + imove[1]), null));
            boolean kill = board.getPiece(new ChessPosition(myPosition.getRow() + imove[0],
                    myPosition.getColumn() + imove[1])) != null;
            imove[0] += Integer.compare(imove[0], 0);
            imove[1] += Integer.compare(imove[1], 0);

            if(!kill) {
                rr(imove, board, myPosition, moves);
            }
        }
    }

    private void r(int[][] instanceMove, ChessBoard board, ChessPosition myPosition,
                   ArrayList<ChessMove> moves, boolean recursive) {
        if (recursive) {
            for (var imove: instanceMove) {
                rr(imove, board, myPosition, moves);
            }
        } else {
            for (var imove: instanceMove) {
                if(acceptablePos(board, new ChessPosition(myPosition.getRow() + imove[0],
                        myPosition.getColumn() + imove[1]), board.getPiece(myPosition).getTeamColor())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + imove[0],
                            myPosition.getColumn() + imove[1]), null));
                }
            }
        }
    }

    private boolean acceptablePos(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        boolean inBounds = position.getColumn() <= 8 &&
                position.getColumn() >= 1 && position.getRow() <= 8 && position.getRow() >= 1;
        return (inBounds && (board.getPiece(position) == null ||
                board.getPiece(position).getTeamColor() != team));
    }

    private boolean acceptablePosPawn(ChessBoard board, ChessPosition position,
                                      ChessGame.TeamColor team, boolean attacking) {
        boolean inBounds = position.getColumn() <= 8 &&
                position.getColumn() >= 1 && position.getRow() <= 8 && position.getRow() >= 1;
        if (attacking) {
            return (inBounds && (board.getPiece(position) != null &&
                    board.getPiece(position).getTeamColor() != team));
        } else {
            return (inBounds && board.getPiece(position) == null);
        }
    }

    private void promoteMove(ChessMove move, ArrayList<ChessMove> moves) {
        if(move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8) {
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.QUEEN));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.BISHOP));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.ROOK));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.KNIGHT));
        } else {
            moves.add(move);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "piece{" +
                "c=" + pieceColor +
                ", t=" + type +
                '}';
    }
}
