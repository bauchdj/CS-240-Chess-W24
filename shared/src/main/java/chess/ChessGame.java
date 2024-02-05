package chess;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTeam;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currentTeam = TeamColor.BLACK;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return this.currentTeam; }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { this.currentTeam = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private static void doForMoves(Collection<ChessMove> moves, Predicate<Iterator<ChessMove>> predicate) {
        Iterator<ChessMove> moveIterator = moves.iterator();
        while (moveIterator.hasNext()) {
            predicate.test(moveIterator);
        }
    }

    private static void movePiece(ChessBoard board, ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPos);
        if (piece == null) throw new InvalidMoveException("No piece at startPosition, movePiece()");
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) piece = new ChessPiece(piece.getTeamColor(), promotionPiece);

        board.addPiece(endPos, piece);
        board.addPiece(startPos, null);
    }

    private static void moveBack(ChessBoard board, ChessMove move) throws InvalidMoveException {
        movePiece(board, new ChessMove(move.getEndPosition(), move.getStartPosition(), move.getPromotionPiece()));
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // TODO loop through each move, apply said move to board, and see if current team is in check.
        // TODO if they are in check then that move must be removed.
        ChessBoard ogBoard = this.board;
        this.board = ogBoard.copy();
        ChessPiece piece = this.board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(this.board, startPosition);

        ChessGame.doForMoves(moves, moveIterator -> {
            ChessMove move = moveIterator.next();
			try { movePiece(this.board, move); }
            catch (InvalidMoveException e) { throw new RuntimeException(e); }

			if (isInCheck(this.currentTeam)) moveIterator.remove();

			try { moveBack(this.board, move); }
            catch (InvalidMoveException e) { throw new RuntimeException(e); }
            // Intellij asked me to include return
			return false;
		});

        this.board = ogBoard;
        return moves;
    }

    private static boolean isMoveinSet(Collection<ChessMove> moves, ChessMove move) {
        Iterator<ChessMove> moveIterator = moves.iterator();
        while (moveIterator.hasNext()) {
            ChessMove moveInMoves = moveIterator.next();
            if (move == moveInMoves) return true;
        }
        return false;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // TODO see if move is in validMoves
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (isMoveinSet(moves, move)) movePiece(this.board, move);
        throw new InvalidMoveException("Invalid move attempted");
    }

    private static ChessPosition findKing(ChessBoard board, TeamColor teamColor) {
        for (int row = 1; row < 9; ++row) {
            for (int column = 1; column < 9; ++column) {
                ChessPosition pos = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) return pos;
            }
        }
        System.out.println("ERROR!!! Line 85 ChessGame : Should always find King, this should NEVER print.");
        return new ChessPosition(1,1);
    }

    private static boolean isKingOfTeamInCheck(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPos = findKing(board, teamColor);

        for (int row = 1; row < 9; ++row) {
            for (int column = 1; column < 9; ++ column) {
                ChessPosition pos = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) continue;

                // TODO Only opposing team pieces
                if (piece.getTeamColor() == teamColor) continue;

                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                Iterator<ChessMove> moveIterator = moves.iterator();

                while (moveIterator.hasNext()) {
                    ChessMove move = moveIterator.next();
                    if (move.getEndPosition().equals(kingPos)) return true;
                }
            }
        }

        return false;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // TODO Could be wrong? IDK
        // TODO you are not in check IF you can take their KING
        //if (isKingOfTeamInCheck(this.board, opponentColor)) return false;

        // TODO you are in check IF you can't take opponents KING and they can take your KING
        if (isKingOfTeamInCheck(this.board, teamColor)) return true;

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // TODO if all team pieces move and still in check then Checkmate baby
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // TODO if there are 0 validMoves for all team pieces
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { this.board = board; }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return this.board; }
}
