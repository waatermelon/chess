import chess.*;
import ui.ChessLoop;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ChessLoop chessLoop = new ChessLoop(0, false);
        chessLoop.run();
    }
}