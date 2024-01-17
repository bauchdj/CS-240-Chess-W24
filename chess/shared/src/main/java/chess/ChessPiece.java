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

    public void pieceMoved() { this.beenMoved = true; }

    public boolean firstMove(int row) {
        int initialRow = this.color == ChessGame.TeamColor.WHITE ? 2 : 7;
        return row == initialRow && !this.beenMoved;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        Collection<ChessMove> moves = new HashSet<>();

        switch (type) {
            case PAWN -> pawnMove(board, color, myPosition, row, col, moves);
            case ROOK -> sideToSideMove(board, myPosition, row, col, moves);
            case BISHOP -> diagonalMove(board, myPosition, row, col, moves);
            case QUEEN -> queenMove(board, myPosition, row, col, moves);
            case KNIGHT -> knightMove(board, myPosition, row, col, moves);
            case KING -> kingMove(board, myPosition, row, col, moves);
        };

        piece.pieceMoved();

        return moves;
    }

    private static boolean isValidIndex(int row, int col) { return !ChessBoard.isValidRowCol(row - 1, col - 1); }

    private static ChessPiece pieceAtRowCol(ChessBoard board, int row, int col) {
        ChessPosition pos = new ChessPosition(row, col);
        return board.getPiece(pos);
    }
    private static boolean noPieceAtRowCol(ChessBoard board, int row, int col) {
        return pieceAtRowCol(board, row, col) == null;
    }

    private static boolean isOpponentAtRowCol(ChessBoard board, ChessPosition myPosition, int row, int col) {
        ChessPiece piece = pieceAtRowCol(board, row, col);
        return piece != null && piece.color != board.getPiece(myPosition).color;
    }

    private static void addPromotionMovesPAWN(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        ChessPosition newPos = new ChessPosition(row, col);
        ChessMove queenMove = new ChessMove(myPosition, newPos, PieceType.QUEEN);
        moves.add(queenMove);
        ChessMove bishopMove = new ChessMove(myPosition, newPos, PieceType.BISHOP);
        moves.add(bishopMove);
        ChessMove rookMove = new ChessMove(myPosition, newPos, PieceType.ROOK);
        moves.add(rookMove);
        ChessMove knightMove = new ChessMove(myPosition, newPos, PieceType.KNIGHT);
        moves.add(knightMove);
    }
    private static void addMove(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        ChessPosition newPos = new ChessPosition(row, col);
        ChessMove move = new ChessMove(myPosition, newPos, null);
        moves.add(move);
    }

    private static void addValidMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        if (isValidIndex(row, col)) {
            //boolean isOpponentThere = isPieceAtRowCol(board, row, col); // TODO function separate for pieces: opponent vs you
            // TODO Cannot move if same color
            //if () {
            //    addMove(myPosition, row, col, moves);
            //}
        }
    }

    private static boolean addValidMovePAWN(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, int oppositeSide, boolean captureOpponent) {
        if (isValidIndex(row, col)) {
            // TODO If opposite side then I need to add 4 moves for QUEEN, BISHOP, ROOK, KNIGHT
            boolean promote = row == oppositeSide;
			// TODO PAWN move forward if no piece is there
            if (!captureOpponent && noPieceAtRowCol(board, row, col)) {
                if (promote) { addPromotionMovesPAWN(myPosition, row, col, moves); }
                else { addMove(myPosition, row, col, moves); }
                return true;
            }
            // TODO PAWN move diagonal if opponent is there
            if (captureOpponent && isOpponentAtRowCol(board, myPosition, row, col)) {
                if (promote) { addPromotionMovesPAWN(myPosition, row, col, moves); }
                else { addMove(myPosition, row, col, moves); }
                return true;
            }
        }
        return false;
    }

    private static void pawnMove(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // System.out.println("PAWN");
        int forward = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        // TODO Add promotion function : You should have 4 moves, one of each type (QUEEN, ROOK, BISHOP, KNIGHT)
        int oppositeSide = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int newRow = row + forward;
        // TODO 1 forward-left || 1 forward-right if opponent there
        // TODO Future tests may require RIGHT then LEFT (-1, 1 is LEFT then RIGHT)
        for (int i = -1; i < 2; i += 2) {
            int newCol = col + i;
            addValidMovePAWN(board, myPosition, newRow, newCol, moves, oppositeSide, true);
        }
        // TODO 1 forward
        boolean movedForwardOne = addValidMovePAWN(board, myPosition, newRow, col, moves, oppositeSide, false);
        // TODO first move allows 2 forward and 1 forward
        if (movedForwardOne) {
            newRow += forward;
            boolean isFirstMove = board.getPiece(myPosition).firstMove(myPosition.getRow());
            if (isFirstMove) addValidMovePAWN(board, myPosition, newRow, col, moves, oppositeSide, false);
        }
    }

    private static void sideToSideMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves side to side
        System.out.println("ROOK");
        // Calc where I can move
        // Create ChessMove Object if move valid
        //
    }

    private static void diagonalMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves diagonally
        System.out.println("BISHOP");
    }

    private static void queenMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves side to side || diagonally
        System.out.println("QUEEN");
    }

    private static void knightMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // up to 8 places: 2 in one direction, 1 in the other
        System.out.println("KNIGHT");
    }

    private static void kingMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // up to 8 places: 1 forward, right, back, left &&
        // forward-right/right-forward, forward-left/left-forward, back-right/right-back, back-left/left-back
        System.out.println("KING");
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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                ", beenMoved=" + beenMoved +
                '}';
    }
}
