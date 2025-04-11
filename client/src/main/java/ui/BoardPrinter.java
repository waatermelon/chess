package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    public BoardPrinter() {
    }

    private String getPiecePrint(ChessPiece piece) {
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        switch (piece.getPieceType()) {
            case ROOK:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_ROOK : WHITE_ROOK;
            case KNIGHT:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_KNIGHT : WHITE_KNIGHT;
            case BISHOP:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_BISHOP : WHITE_BISHOP;
            case QUEEN:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_QUEEN : WHITE_QUEEN;
            case KING:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_KING : WHITE_KING;
            case PAWN:
                return pieceColor == ChessGame.TeamColor.BLACK ? BLACK_PAWN : WHITE_PAWN;
            default:
                return "\u2003";
        }
    }

    private void setBoardSlots(GameData gameData, String[][] board, ChessGame.TeamColor teamColor) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boolean notPrinted = true;
                if (i == 0 || i == board.length - 1 || j == 0 || j == board[i].length - 1) {
                    board[i][j] = SET_BG_COLOR_MAGENTA;
                } else {
                    boolean isEven = (i + j) % 2 == 0;
                    board[i][j] = isEven ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY;
                }

                if ((i == 0 || i == board.length - 1) && (j > 0 && j < board[i].length - 1)) {
                    char columnLabel;
                    if (teamColor == ChessGame.TeamColor.WHITE) {
                        columnLabel = (char) ('a' + j - 1);
                    } else {
                        columnLabel = (char) ('h' - (j - 1));
                    }
                    board[i][j] += SET_TEXT_COLOR_BLACK + "\u2003" + columnLabel + " ";
                    notPrinted = false;
                }

                if ((j == 0 || j == board[i].length - 1) && (i > 0 && i < board.length - 1)) {
                    int rowNumber = (teamColor == ChessGame.TeamColor.WHITE) ? (9 - i) : i;
                    char rowLabel = (char) ('0' + rowNumber);
                    board[i][j] += SET_TEXT_COLOR_BLACK + "\u2003" + rowLabel + "\u2003";
                    notPrinted = false;
                }

                if (i > 0 && i < board.length - 1 && j > 0 && j < board[i].length - 1) {
                    int chessRow, chessCol;
                    if (teamColor == ChessGame.TeamColor.WHITE) {
                        chessRow = 9 - i;
                        chessCol = j;
                    } else {
                        chessRow = i;
                        chessCol = 9 - j;
                    }
                    ChessPosition position = new ChessPosition(chessRow, chessCol);
                    ChessPiece piece = gameData.game().getBoard().getPiece(position);

                    if (piece != null) {
                        board[i][j] += getPiecePrint(piece);
                    } else {
                        board[i][j] += "\u2003  ";
                    }
                } else if(notPrinted) {
                    board[i][j] += "\u2003 \u2003";
                }
            }
        }
    }

    public void printBoard(GameData gameData, ChessGame.TeamColor teamColor) {
        String[][] board = new String[10][10];
        setBoardSlots(gameData, board, teamColor);

        for (String[] row : board) {
            for (String cell : row) {
                System.out.print(cell);
            }
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }
    }

    private void setBoardSlotsHighlighted(GameData gameData, String[][] board,
                                          ChessGame.TeamColor teamColor, Collection<ChessMove> moves) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boolean notPrinted = true;
                if (i == 0 || i == board.length - 1 || j == 0 || j == board[i].length - 1) {
                    board[i][j] = SET_BG_COLOR_MAGENTA;
                } else {
                    boolean isEven = (i + j) % 2 == 0;
                    board[i][j] = isEven ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY;
                }

                if ((i == 0 || i == board.length - 1) && (j > 0 && j < board[i].length - 1)) {
                    char columnLabel;
                    if (teamColor == ChessGame.TeamColor.WHITE) {
                        columnLabel = (char) ('a' + j - 1);
                    } else {
                        columnLabel = (char) ('h' - (j - 1));
                    }
                    board[i][j] += SET_TEXT_COLOR_BLACK + "\u2003" + columnLabel + " ";
                    notPrinted = false;
                }

                if ((j == 0 || j == board[i].length - 1) && (i > 0 && i < board.length - 1)) {
                    int rowNumber = (teamColor == ChessGame.TeamColor.WHITE) ? (9 - i) : i;
                    char rowLabel = (char) ('0' + rowNumber);
                    board[i][j] += SET_TEXT_COLOR_BLACK + "\u2003" + rowLabel + "\u2003";
                    notPrinted = false;
                }

                if (i > 0 && i < board.length - 1 && j > 0 && j < board[i].length - 1) {
                    int chessRow, chessCol;
                    if (teamColor == ChessGame.TeamColor.WHITE) {
                        chessRow = 9 - i;
                        chessCol = j;
                    } else {
                        chessRow = i;
                        chessCol = 9 - j;
                    }
                    ChessPosition position = new ChessPosition(chessRow, chessCol);
                    boolean isAvailable = moves.stream()
                            .anyMatch(move -> move.getEndPosition().equals(position));

                    if (isAvailable) {
                        boolean isEven = (i + j) % 2 == 0;
                        board[i][j] = isEven ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_MAGENTA;
                    } else {
                        boolean isEven = (i + j) % 2 == 0;
                        board[i][j] = isEven ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY;
                    }

                    ChessPiece piece = gameData.game().getBoard().getPiece(position);
                    if (piece != null) {
                        board[i][j] += getPiecePrint(piece);
                    } else {
                        board[i][j] += "\u2003  ";
                    }
                } else if (notPrinted) {
                    board[i][j] += "\u2003 \u2003";
                }
            }
        }
    }

    public void printHighlightedBoard(
            GameData gameData, ChessGame.TeamColor teamColor, ChessPosition pos
    )
    {
        String[][] board = new String[10][10];
        Collection<ChessMove> moves = gameData.game().validMoves(pos);
        setBoardSlotsHighlighted(gameData, board, teamColor, moves);

        for (String[] row : board) {
            for (String cell : row) {
                System.out.print(cell);
            }
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }
    }
}