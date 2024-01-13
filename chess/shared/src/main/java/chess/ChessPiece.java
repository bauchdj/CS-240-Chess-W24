package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;
    private static final int BOARD_SIZE = 8;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
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

        return switch (type) {
            case PAWN -> pawnMove(board, color, myPosition, row, col, moves);
            case ROOK -> sideToSideMove(board, myPosition, row, col, moves);
            case BISHOP -> diagonalMove(board, myPosition, row, col, moves);
            case QUEEN -> queenMove(board, myPosition, row, col, moves);
            case KNIGHT -> knightMove(board, myPosition, row, col, moves);
            case KING -> kingMove(board, myPosition, row, col, moves);
        };
    }

    private static boolean isValidIndex(int row, int col) {
        int limit = BOARD_SIZE - 1;
        return !(row < 0 || row > limit || col < 0 || col > limit);
    }
    private static boolean isPieceAtRowCol(ChessBoard board, int row, int col) {
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(pos);
        return piece != null;
    }

    private static void addValidMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, boolean opponentRequiredForMove) {
        if (isValidIndex(row, col)) {
            boolean notNeeded = !opponentRequiredForMove;
            if (notNeeded || isPieceAtRowCol(board, row, col)) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessMove move = new ChessMove(myPosition, newPos, null);
                moves.add(move);
            }
        }
    }

    private static Collection<ChessMove> pawnMove(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        System.out.println("PAWN");
        int forward = (color == ChessGame.TeamColor.BLACK) ? 1 : -1;
        int oppositeSide = (color == ChessGame.TeamColor.BLACK) ? 7 : 0;
        // 1 forward
        int newRow = row + forward;
        addValidMove(board, myPosition, newRow, col, moves, false);
        // 1 forward-right || 1 forward-left if opponent there
        for (int i = -1; i < 2; i += 2) {
            int newCol = col + i;
            addValidMove(board, myPosition, newRow, newCol, moves, true);
        }
        // first move allows 2 forward and 1 forward
        // TODO Finish first time pawn is moved
        return moves;
    }

    private static Collection<ChessMove> sideToSideMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves side to side
        System.out.println("ROOK");
        // Calc where I can move
        // Create ChessMove Object if move valid
        //
        return moves;
    }

    private static Collection<ChessMove> diagonalMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves diagonally
        System.out.println("BISHOP");
        return moves;
    }

    private static Collection<ChessMove> queenMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // moves side to side || diagonally
        System.out.println("QUEEN");
        return moves;
    }

    private static Collection<ChessMove> knightMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // up to 8 places: 2 in one direction, 1 in the other
        System.out.println("KNIGHT");
        return moves;
    }

    private static Collection<ChessMove> kingMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves) {
        // up to 8 places: 1 forward, right, back, left &&
        // forward-right/right-forward, forward-left/left-forward, back-right/right-back, back-left/left-back
        System.out.println("KING");
        return moves;
    }
}
