package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;
    private boolean beenMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        this.beenMoved = false;
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
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    public void pieceMoved() {
        this.beenMoved = true;
    }

    private boolean isFirstMove(ChessPosition myPosition) {
        int mySide = (this.color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        boolean isMySide = (myPosition.getRow() == mySide);
        return !this.beenMoved && isMySide;
    }

    private static boolean isPieceThere(ChessBoard board, int row, int col) {
        ChessPiece pieceThere = board.getPiece(new ChessPosition(row, col));
        return pieceThere != null;
    }

    private static boolean isOpponentThere(ChessBoard board, ChessPosition myPosition, int row, int col) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessPiece pieceThere = board.getPiece(new ChessPosition(row, col));
        if (pieceThere == null) return false;
        return myPiece.color != pieceThere.color;
    }

    private static boolean isMyTeamThere(ChessBoard board, ChessPosition myPosition, int row, int col) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessPiece pieceThere = board.getPiece(new ChessPosition(row, col));
        if (pieceThere == null) return false;
        return myPiece.color == pieceThere.color;
    }

    private static void addMove(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        ChessPosition endPos = new ChessPosition(row, col);
        moves.add(new ChessMove(myPosition, endPos, null));
    }

    private static void addPawnPromotionMoves(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        ChessPosition endPos = new ChessPosition(row, col);
        moves.add(new ChessMove(myPosition, endPos, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, endPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPos, ChessPiece.PieceType.KNIGHT));
    }

    private static void pawnDiagonalTakeOpponent(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, int oppositeSide) {
        if (!ChessBoard.isValidPosition(new ChessPosition(row, col))) return;
        if (!isOpponentThere(board, myPosition, row, col)) return;

        if (row == oppositeSide) addPawnPromotionMoves(myPosition, row, col, moves);
        else addMove(myPosition, row, col, moves);
    }

    private static boolean pawnMoveForward(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, int oppositeSide) {
        if (!ChessBoard.isValidPosition(new ChessPosition(row, col))) return false;
        if (isPieceThere(board, row, col)) return false;

        if (row == oppositeSide) addPawnPromotionMoves(myPosition, row, col, moves);
        else addMove(myPosition, row, col, moves);
        return true;
    }

    private static void pawnMove(ChessBoard board, ChessPiece piece, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // Pawn must move forward
        int forward = (piece.color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        // Opposing side
        int oppositeSide = (piece.color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        row += forward;

        // Move diagonal if opponent there
        pawnDiagonalTakeOpponent(board, myPosition, row, col - 1, moves, oppositeSide);
        pawnDiagonalTakeOpponent(board, myPosition, row, col + 1, moves, oppositeSide);

        // Move forward 1
        boolean canMoveForwardOne = pawnMoveForward(board, myPosition, row, col, moves, oppositeSide);

        // Move forward 2
        if (piece.isFirstMove(myPosition) && canMoveForwardOne) {
            row += forward;
            pawnMoveForward(board, myPosition, row, col, moves, oppositeSide);
        }
    }

    private static void fullBoardMove(ChessBoard board, ChessPosition myPosition, int row, int col, int rowDelta, int colDelta, Collection<ChessMove> moves) {
        row += rowDelta;
        col += colDelta;

        while (ChessBoard.isValidPosition(new ChessPosition(row, col))) {
            if (isOpponentThere(board, myPosition, row, col)) {
                addMove(myPosition, row, col, moves);
                break;
            } else if (!isPieceThere(board, row, col)) {
                addMove(myPosition, row, col, moves);
                row += rowDelta;
                col += colDelta;
            } else {
                break;
            }
        }
    }

    private static void sideToSideMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        fullBoardMove(board, myPosition, row, col, 1, 0, moves);
        fullBoardMove(board, myPosition, row, col, -1, 0, moves);
        fullBoardMove(board, myPosition, row, col, 0, 1, moves);
        fullBoardMove(board, myPosition, row, col, 0, -1, moves);
    }

    private static void diagonalMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        fullBoardMove(board, myPosition, row, col, 1, 1, moves);
        fullBoardMove(board, myPosition, row, col, 1, -1, moves);
        fullBoardMove(board, myPosition, row, col, -1, 1, moves);
        fullBoardMove(board, myPosition, row, col, -1, -1, moves);
    }

    private static void queenMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        sideToSideMove(board, myPosition, row, col, moves);
        diagonalMove(board, myPosition, row, col, moves);
    }

    private static void addValidMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        if (!ChessBoard.isValidPosition(new ChessPosition(row, col))) return;
        if (isMyTeamThere(board, myPosition, row, col)) return;

        addMove(myPosition, row, col, moves);
    }

    private static void knightMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        addValidMove(board, myPosition, row + 2, col + 1, moves);
        addValidMove(board, myPosition, row + 2, col - 1, moves);
        addValidMove(board, myPosition, row - 2, col + 1, moves);
        addValidMove(board, myPosition, row - 2, col - 1, moves);
        addValidMove(board, myPosition, row + 1, col + 2, moves);
        addValidMove(board, myPosition, row + 1, col - 2, moves);
        addValidMove(board, myPosition, row - 1, col + 2, moves);
        addValidMove(board, myPosition, row - 1, col - 2, moves);
    }

    private static void kingMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        addValidMove(board, myPosition, row + 1, col, moves);
        addValidMove(board, myPosition, row - 1, col, moves);
        addValidMove(board, myPosition, row, col + 1, moves);
        addValidMove(board, myPosition, row, col - 1, moves);
        addValidMove(board, myPosition, row + 1, col + 1, moves);
        addValidMove(board, myPosition, row + 1, col - 1, moves);
        addValidMove(board, myPosition, row - 1, col + 1, moves);
        addValidMove(board, myPosition, row - 1, col - 1, moves);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (this.type) {
            case PAWN -> pawnMove(board, this, myPosition, row, col, moves);
            case ROOK -> sideToSideMove(board, myPosition, row, col, moves);
            case BISHOP -> diagonalMove(board, myPosition, row, col, moves);
            case QUEEN -> queenMove(board, myPosition, row, col, moves);
            case KNIGHT -> knightMove(board, myPosition, row, col, moves);
            case KING -> kingMove(board, myPosition, row, col, moves);
        }

        return moves;
    }

    @Override
    public String toString() {
        char representation;
		representation = switch (this.type) {
			case PAWN -> 'p';
			case KNIGHT -> 'n';
			case BISHOP -> 'b';
			case ROOK -> 'r';
			case QUEEN -> 'q';
			case KING -> 'k';
		};


        if (this.color == ChessGame.TeamColor.WHITE) representation = Character.toUpperCase(representation);

        return String.valueOf(representation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return beenMoved == that.beenMoved && color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type, beenMoved);
    }
}
