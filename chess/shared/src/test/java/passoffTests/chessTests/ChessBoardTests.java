package passoffTests.chessTests;

import chess.*;
import org.junit.jupiter.api.*;

import static passoffTests.TestFactory.*;

public class ChessBoardTests {

    @Test
    @DisplayName("Add and Get Piece")
    public void getAddPiece() {
        ChessPosition position = getNewPosition(4, 4);
        ChessPiece piece = getNewPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        var board = getNewBoard();
        board.addPiece(position, piece);

        ChessPiece foundPiece = board.getPiece(position);

        Assertions.assertEquals(piece.getPieceType(), foundPiece.getPieceType(),
                "ChessPiece returned by getPiece had the wrong piece type");
        Assertions.assertEquals(piece.getTeamColor(), foundPiece.getTeamColor(),
                "ChessPiece returned by getPiece had the wrong team color");
    }


    @Test
    @DisplayName("Reset Board")
    public void defaultGameBoard() {
        var expectedBoard = loadBoard("""
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |P|P|P|P|P|P|P|P|
                |R|N|B|Q|K|B|N|R|
                """);

        var actualBoard = getNewBoard();
        actualBoard.resetBoard();

        /*
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var epiece = expectedBoard.getPiece(new ChessPosition(i, j));
                var apiece = actualBoard.getPiece(new ChessPosition(i, j));
                ChessPiece.PieceType etype = null;
                ChessPiece.PieceType atype = null;
                ChessGame.TeamColor ecolor = null;
                ChessGame.TeamColor acolor = null;
                if (epiece != null) {
                    etype = epiece.getPieceType();
                    atype = apiece.getPieceType();
                    ecolor = epiece.getTeamColor();
                    acolor = apiece.getTeamColor();
                }
                String e = etype + ":" + ecolor;
                String a = atype + ":" + acolor;
                if (!a.equals(e)) { System.out.println("\nDIFF!!! -> " + e + " | " + a + "\n"); break; }
                System.out.println(">>> Row: " + i + ", Col: " + j + " | Expected: " + e + ", Actual: " + a);
            }
        }
        */

        Assertions.assertEquals(expectedBoard, actualBoard);
    }

}
