package chess.helper;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PieceCalculator {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor teamColor;
    private final ChessBoard board;

    public PieceCalculator(ChessPiece.PieceType type, ChessGame.TeamColor teamColor, ChessBoard board) {
        this.type = type;
        this.teamColor = teamColor;
        this.board = board;
    }


    /***
     *
     * @param position  The current position of the piece.
     * @param row       The row(y/i) movement. (-1, 0, 1)
     * @param col       The column(x/j) movement. (-1, 0, 1)
     * @return          The possible moves based off of the given row and column directions.
     */
    private ArrayList<ChessMove> directionalMove(ChessPosition position, int row, int col) {
        int verticalDist = 0;
        int horizontalDist = 0;
        boolean rook = false;
        if(row != 0) verticalDist = (row > 0) ? 8 - position.getRow(): position.getRow() - 1;
        else rook = true;
        if(col != 0) horizontalDist = (col > 0) ? 8 - position.getColumn(): position.getColumn() - 1;
        else rook = true;

        int[][] moves = new int[(rook) ? Math.max(verticalDist, horizontalDist) : Math.min(verticalDist, horizontalDist)][];
        for(int i = 0; i < moves.length; ++i) {
            moves[i] = new int[]{
                    (rook) ? position.getRow() - 1 : (row > 0) ? 7 : 0,
                    (rook) ? position.getColumn() - 1 : (col > 0) ? 7 : 0
            };
        }

        for(int i = position.getRow() - 1 + row, j = 0; (j < moves.length) && (row != 0) && ((row > 0) ? (i < 8) : (i > -1)); i += row) {
            moves[j][0] = i;
            j++;
        }
        for(int i = position.getColumn() - 1 + col, j = 0; (j < moves.length) && (col != 0) && ((col > 0) ? (i < 8) : (i > -1)); i += col) {
            moves[j][1] = i;
            j++;
        }

        ArrayList<ChessMove> movesList = new ArrayList<>();
        for (int[] move : moves)
            movesList.add(new ChessMove(position, new ChessPosition(move[0] + 1, move[1] + 1), null));

        return movesList;
    }

    private Collection<ChessMove> filterMoves(ArrayList<ChessMove> moves) {
        ArrayList<ChessMove> movesList = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessPiece other = board.getPiece(move.endPosition());
            if (other != null && other.getTeamColor() != teamColor){
                movesList.add(move);
                break;
            } else if (other != null) {
                break;
            }
            movesList.add(move);
        }

        return movesList;
    }

    private Collection<ChessMove> removeIllegalMoves(Collection<ChessMove> moves) {
        ArrayList<ChessMove> movesList = new ArrayList<>();
        for (ChessMove move : moves) {
            if (move.endPosition().getRow() > 8 || move.endPosition().getRow() < 1) continue;
            if (move.endPosition().getColumn() > 8 || move.endPosition().getColumn() < 1) continue;

            ChessPiece other = board.getPiece(move.endPosition());
            if (other != null && other.getTeamColor() != teamColor) {
                movesList.add(move);
            } else if (other == null) {
                movesList.add(move);
            }
        }

        return movesList;
    }

    private Collection<ChessMove> removeIllegalMovesPawn(Collection<ChessMove> moves) {

        ArrayList<ChessMove> movesList = new ArrayList<>();

        for (ChessMove move : moves) {
            if (move.endPosition().getRow() > 8 || move.endPosition().getRow() < 1) continue;
            if (move.endPosition().getColumn() > 8 || move.endPosition().getColumn() < 1) continue;

            ChessPiece other = board.getPiece(move.endPosition());
            boolean diagonal = move.startPosition().getColumn() != move.endPosition().getColumn();

            if (diagonal && other != null && other.getTeamColor() != teamColor) {
                movesList.add(move);
            } else if (!diagonal && other == null) {
                movesList.add(move);
            }
        }
        return movesList;
    }

    private Collection<ChessMove> getPromotions(Collection<ChessMove> moves) {
        ArrayList<ChessMove> movesList = new ArrayList<>();
        for (ChessMove move: moves) {
            if (move.endPosition().getRow() == 1 || move.endPosition().getRow() == 8) {
                movesList.add(new ChessMove(move.startPosition(), move.endPosition(), ChessPiece.PieceType.QUEEN));
                movesList.add(new ChessMove(move.startPosition(), move.endPosition(), ChessPiece.PieceType.BISHOP));
                movesList.add(new ChessMove(move.startPosition(), move.endPosition(), ChessPiece.PieceType.ROOK));
                movesList.add(new ChessMove(move.startPosition(), move.endPosition(), ChessPiece.PieceType.KNIGHT));
            } else {
                movesList.add(move);
            }
        }
        return movesList;
    }

    private Collection<ChessMove> pawnMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int yChange = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        boolean doubleJump = teamColor == ChessGame.TeamColor.WHITE && position.getRow() == 2 || teamColor == ChessGame.TeamColor.BLACK && position.getRow() == 7;

        for (int i = -1; i < 2; ++i)
            moves.add(new ChessMove(position, new ChessPosition(position.getRow() + yChange, position.getColumn() + i), null));

        if(doubleJump && board.getPiece(moves.get(1).endPosition()) == null) {
            moves.add(new ChessMove(position, new ChessPosition(position.getRow() + (yChange * 2), position.getColumn()), null));
        }
        return getPromotions(removeIllegalMovesPawn(moves));
    }

    private Collection<ChessMove> knightMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 0, j = 2, k = 1; i < 8; ++i) {
            moves.add(new ChessMove(position, new ChessPosition(position.getRow() + j, position.getColumn() + k*((Math.abs(j) == 1) ? 2 : 1)), null));
            k *= -1;
            if (i % 2 == 1) j *= -1;
            if (i == 3) j = 1;
        }
        return removeIllegalMoves(moves);
    }

    private Collection<ChessMove> rookMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(filterMoves(directionalMove(position, 0, -1)));
        moves.addAll(filterMoves(directionalMove(position, 0, 1)));
        moves.addAll(filterMoves(directionalMove(position, -1, 0)));
        moves.addAll(filterMoves(directionalMove(position, 1, 0)));

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(filterMoves(directionalMove(position, 1, -1)));
        moves.addAll(filterMoves(directionalMove(position, 1, 1)));
        moves.addAll(filterMoves(directionalMove(position, -1, -1)));
        moves.addAll(filterMoves(directionalMove(position, -1, 1)));

        return moves;
    }
    private Collection<ChessMove> kingMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (i == 0 && j == 0) continue;
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + i, position.getColumn() + j), null));
            }
        }
        return removeIllegalMoves(moves);
    }
    private Collection<ChessMove> queenMoves(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(bishopMoves(position));
        moves.addAll(rookMoves(position));

        return moves;
    }

    public static Collection<ChessMove> possibleMoves(ChessPiece.PieceType type, ChessGame.TeamColor teamColor, ChessPosition position, ChessBoard board) {
        PieceCalculator pc = new PieceCalculator(type, teamColor, board);
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
