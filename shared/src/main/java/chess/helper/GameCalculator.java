package chess.helper;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class GameCalculator {

    private final ChessBoard board;

    public GameCalculator(ChessBoard board) {
        this.board = board;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor team) {
        ChessPiece[][] boardArr = board.getBoard();
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j) {
                ChessPiece piece = boardArr[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == team) {
                    return new ChessPosition(i + 1, j + 1);
                }
            }
        }
        return null;
    }


    public ArrayList<ChessPosition> getKingThreats(ChessGame.TeamColor team) {
        ChessPiece[][] boardArr = board.getBoard();
        ArrayList<ChessPosition> threats = new ArrayList<>();

        for(int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                ChessPiece piece = boardArr[i][j];
                if (piece == null || piece.getTeamColor() == team) {
                    continue;
                }
                ChessPosition kingPos = getKingPosition(team);
                for (ChessMove move : piece.pieceMoves(board,new ChessPosition(i + 1, j + 1))) {
                    if (move.getEndPosition().equals(kingPos)) {
                        threats.add(new ChessPosition(i + 1, j + 1));
                        break;
                    }
                }

            }
        }
        return threats;
    }

    public ArrayList<ChessPosition> getEnemyPositions(ChessGame.TeamColor team) {
        ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
        ChessPiece[][] boardArr = board.getBoard();
        for(int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                ChessPiece current = boardArr[i][j];
                if(current == null || current.getTeamColor() == team) {
                    continue;
                }
                allowProduction(current, enemyPositions, i, j);
            }
        }
        return enemyPositions;
    }

    public void allowProduction(ChessPiece current, ArrayList<ChessPosition> enemyPositions, int i, int j) {
        for(ChessMove move: current.pieceMoves(board,
                new ChessPosition(i + 1, j + 1))) {
            boolean contained = false;
            for(ChessPosition pos: enemyPositions) {
                if(move.getEndPosition().equals(pos)) {
                    contained = true;
                    break;
                }
            }
            if(!contained) {
                enemyPositions.add(move.getEndPosition());
            }
        }
    }

    public ArrayList<ChessMove> getMovePositionNonOverlap(ArrayList<ChessPosition> positions,
                                                          ArrayList<ChessMove> moves) {
        ArrayList<ChessMove> nonOverlap = new ArrayList<>();
        for (ChessMove move : moves) {
            boolean foundOverlap = false;
            for (ChessPosition position : positions) {
                if (position.equals(move.getEndPosition())) {
                    foundOverlap = true;
                    break;
                }
            }
            if(!foundOverlap) {
                nonOverlap.add(move);
            }
        }
        return nonOverlap;
    }

    public ArrayList<ChessMove> getKingMoves(ChessGame.TeamColor team) {

        ArrayList<ChessPosition> enemyPositions = getEnemyPositions(team);
        ChessPosition kingPos = getKingPosition(team);
        ChessPiece king = board.getPiece(kingPos);
        Collection<ChessMove> kingMoves = king.pieceMoves(board, getKingPosition(team));
        ArrayList<ChessMove> newKingMoves = new ArrayList<>();

        for(ChessMove move: kingMoves) {
            board.addPiece(kingPos, null);
            ChessPiece temp = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), king);
            if(isInCheck(team)) {
                board.addPiece(kingPos, king);
                board.addPiece(move.getEndPosition(), temp);
            } else {
                board.addPiece(kingPos, king);
                board.addPiece(move.getEndPosition(), temp);
                newKingMoves.add(move);
            }
        }
        return getMovePositionNonOverlap(enemyPositions, newKingMoves);
    }

    public boolean isInCheck(ChessGame.TeamColor team) {
        ArrayList<ChessPosition> kingThreats = getKingThreats(team);
        return !kingThreats.isEmpty();
    }

    public boolean moveAllowed(ChessMove move, ChessGame.TeamColor team) {
        ChessPiece current = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        ChessPiece temp = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), current);
        boolean isAllowed = !isInCheck(team);
        board.addPiece(move.getEndPosition(), temp);
        board.addPiece(move.getStartPosition(), current);
        return isAllowed;
    }

    public boolean isInStalemate(ChessGame.TeamColor team) {
        if (doIt(team, 1)) {
            return false;
        }
        return getKingThreats(team).isEmpty() && getKingMoves(team).isEmpty();
    }

    public boolean doIt(ChessGame.TeamColor team, int number) {
        ChessPiece[][] boardArr = board.getBoard();

        for(int i = 0; i < 8; ++i) {
            for (int j = 0; j  < 8; ++j) {
                ChessPiece current = boardArr[i + number - number][j];
                if (current == null) {
                    continue;
                }
                if(current.getTeamColor() != team) {
                    continue;
                }
                if(current.getPieceType() == ChessPiece.PieceType.KING) {
                    continue;
                }
                if(allowedMoves(current, i, j, team)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private boolean allowedMoves(ChessPiece current, int i, int j, ChessGame.TeamColor team) {
        for(ChessMove move: current.pieceMoves(board, new ChessPosition(i + 1, j + 1))) {
            if( moveAllowed(move, team)){
                return false;
            }
        }
        return true;
    }

    public boolean isPossibleMove(ChessGame.TeamColor team) {
        if (doIt(team, 2)) {
            return false;
        }
        return getKingMoves(team).isEmpty();
    }
}