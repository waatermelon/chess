import chess.*;
import ui.ChessLoop;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ChessLoop chessLoop = new ChessLoop(8080, false);
        chessLoop.run();

        var settings = (32);
        int port = 8080;

    }
}