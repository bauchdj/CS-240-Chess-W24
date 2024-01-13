package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return board[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            ChessPiece.PieceType type = getPieceType(i);
            board[0][i] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
            board[1][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[7][i] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
        }
    }

    private static ChessPiece.PieceType getPieceType(int i) {
		return switch (i) {
			case 0, 7 -> ChessPiece.PieceType.ROOK;
			case 1, 6 -> ChessPiece.PieceType.KNIGHT;
			case 2, 5 -> ChessPiece.PieceType.BISHOP;
			case 3 -> ChessPiece.PieceType.QUEEN;
			case 4 -> ChessPiece.PieceType.KING;
			default -> null;
		};
	}
}
