package chess;

import chess.helper.GameCalculator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor turn = TeamColor.WHITE;

    private boolean gameFinished;

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public boolean getGameFinished() {
        return gameFinished;
    }

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece current = board.getPiece(startPosition);
        ArrayList<ChessMove> validMovesArrayList = new ArrayList<>();

        GameCalculator calc = new GameCalculator(board);
        Collection<ChessMove> currentMoves = current.pieceMoves(board, startPosition);
        ArrayList<ChessPosition> kingThreat = calc.getKingThreats(getTeamTurn());

        for (ChessMove move: current.pieceMoves(board, startPosition)) {
            board.addPiece(startPosition, null);
            ChessPiece posPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), current);
            if(!isInCheck(current.getTeamColor())) {
                validMovesArrayList.add(move);
            }
            board.addPiece(move.getEndPosition(), posPiece);
            board.addPiece(startPosition, current);
        }

        return validMovesArrayList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece current = board.getPiece(move.getStartPosition());
        if(current == null) {
            throw new InvalidMoveException("piece does not exist");
        }
        boolean correctTeam = getTeamTurn() == current.getTeamColor();
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        /*
        restraints:
        - piece exists
        - correct team moving
        - move is valid
        - promotion is valid

        after:
        - set team turn
        - set promotion
        - set position
        - remove previous position
         */
        if(!correctTeam) {
            throw new InvalidMoveException("incorrect team moving");
        }
        if(validMoves.isEmpty()) {
            throw new InvalidMoveException("no valid moves possible for piece");
        }
        if(!validMoves.contains(move)) {
            throw new InvalidMoveException("move not contained");
        }
        if(move.getPromotionPiece() != null) {
            ArrayList<ChessMove> possibleMoves = new ArrayList<>(current.pieceMoves(board, move.getStartPosition()));
            boolean promotionPossible = false;
            for(ChessMove pmove: possibleMoves) {
                if(pmove.getPromotionPiece() == move.getPromotionPiece()) {
                    promotionPossible = true;
                    break;
                }
            }
            if(!promotionPossible) {
                throw new InvalidMoveException("promotion impossible");
            }
        }

        if(move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), current);
        }


        board.addPiece(move.getStartPosition(), null);
        setTeamTurn((getTeamTurn() == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        GameCalculator calc = new GameCalculator(board);
        ArrayList<ChessPosition> kingThreats = calc.getKingThreats(teamColor);
        return !kingThreats.isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        GameCalculator calc = new GameCalculator(board);
        return isInCheck(teamColor) && calc.isPossibleMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        GameCalculator calc = new GameCalculator(board);
        return calc.isInStalemate(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}