package chess;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
//import java.util.function.Predicate;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTeam;
    private ChessMove lastMove;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTeam = TeamColor.WHITE;
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

    private static ChessPosition findKing(ChessBoard board, TeamColor teamColor) {
        for (int row = 1; row < 9; ++row) {
            for (int column = 1; column < 9; ++column) {
                ChessPosition pos = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) continue;
                if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) return pos;
            }
        }
        return null;
    }

    private static boolean staticIsInCheck(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPos = findKing(board, teamColor);
        if (kingPos == null) return false;

        for (int row = 1; row < 9; ++row) {
            for (int column = 1; column < 9; ++column) {
                ChessPosition pos = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) continue;
                if (piece.getTeamColor() == teamColor) continue;

                Collection<ChessMove> moves = piece.pieceMoves(board, pos);

                for (ChessMove move : moves) {
                    if (kingPos.equals(move.getEndPosition())) return true;
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
        return staticIsInCheck(this.board, teamColor);
    }

    private static boolean isPieceInBetween(ChessBoard board, int row, int[] colsToCheck) {
        for (int col : colsToCheck) {
            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isKingInCheckInBetween(ChessBoard board, ChessPiece king, ChessPosition startPos, int[] colsToCheck) {
        for (int col : colsToCheck) {
            ChessBoard boardCopy = board.copy();
            ChessMove move = new ChessMove(startPos, new ChessPosition(startPos.getRow(), col), null);
            try {
                movePiece(boardCopy, move);
            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }
            if (staticIsInCheck(boardCopy, king.getTeamColor())) {
                return true;
            }
        }
        return false;
    }

    private static void checkCastlingSide(ChessBoard board, ChessPiece king, ChessPosition startPos, Collection<ChessMove> moves, boolean queenSide) {
        int row = startPos.getRow();
        int rookCol = queenSide ? 1 : 8;
        int[] colsToCheck = queenSide ? new int[]{2, 3, 4} : new int[]{6, 7};

        ChessPiece rook = board.getPiece(new ChessPosition(row, rookCol));
        if (rook == null || rook.getBeenMoved()) return;

        // Check if any pieces are between the king and rook.
        if (isPieceInBetween(board, row, colsToCheck)) return;

        // Check if king passes through or ends up in check.
        if (isKingInCheckInBetween(board, king, startPos, colsToCheck)) return;

        // Add castling move.
        int castlingCol = queenSide ? 3 : 7;
        moves.add(new ChessMove(startPos, new ChessPosition(row, castlingCol), null));
    }

    private static void addCastlingValidMoves(ChessBoard board, ChessPiece piece, ChessPosition startPos, Collection<ChessMove> moves) {
        // Early exit if the piece is not a king or has been moved already.
        if (piece.getBeenMoved() || piece.getPieceType() != ChessPiece.PieceType.KING) return;

        // Check both sides for castling possibilities.
        checkCastlingSide(board, piece, startPos, moves, true); // Queen-side
        checkCastlingSide(board, piece, startPos, moves, false); // King-side
    }

    private static boolean isEnPassantPossible(ChessBoard board, ChessMove lastMove, ChessPosition startPos, ChessPiece piece) {
        if (lastMove == null || piece.getPieceType() != ChessPiece.PieceType.PAWN) return false;

        ChessPosition lastPos = lastMove.getEndPosition();
        ChessPiece lastPiece = board.getPiece(lastPos);
        if (lastPiece == null || lastPiece.getPieceType() != ChessPiece.PieceType.PAWN) return false;


        int opposingPawnRow = piece.getTeamColor() == TeamColor.WHITE ? 5 : 4;
        return lastPos.getRow() == opposingPawnRow && startPos.getRow() == opposingPawnRow;
    }

    private static void addEnPassantValidMoves(ChessBoard board, ChessMove lastMove, ChessPosition startPos, ChessPiece piece, Collection<ChessMove> moves) {
        if (!isEnPassantPossible(board, lastMove, startPos, piece)) return;

        ChessPosition lastPos = lastMove.getEndPosition();
        int forward = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
        ChessMove enPassantMove = new ChessMove(startPos, new ChessPosition(startPos.getRow() + forward, lastPos.getColumn()), null);

        try {
            movePiece(board, enPassantMove);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }

        if (staticIsInCheck(board, piece.getTeamColor())) return;

        moves.add(enPassantMove);
    }

    private static Collection<ChessMove> staticValidMoves(ChessGame game, ChessBoard board, ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        Iterator<ChessMove> moveIterator = moves.iterator();
        while (moveIterator.hasNext()) {
            ChessBoard boardCopy = board.copy();

            ChessMove move = moveIterator.next();
            try { movePiece(boardCopy, move); }
            catch (InvalidMoveException e) { throw new RuntimeException(e); }

            if (staticIsInCheck(boardCopy, piece.getTeamColor())) moveIterator.remove();
        }

        addCastlingValidMoves(board, piece, startPosition, moves);
        addEnPassantValidMoves(board.copy(), game.lastMove, startPosition, piece, moves);

        return moves;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return staticValidMoves(this, this.board.copy(), startPosition);
    }

    private static void executeEnPassantMove(ChessBoard board, ChessMove lastMove, ChessMove move, ChessPiece piece) {
        if (!isEnPassantPossible(board, lastMove, move.getStartPosition(), piece)) return;

        ChessPosition lastPos = lastMove.getEndPosition();
        int forward = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
        ChessPosition endPos = move.getEndPosition();

        // If moving forward and to the same column as the opposing pawn
        if (endPos.getRow() == (lastPos.getRow() + forward) && endPos.getColumn() == lastPos.getColumn()) {
            board.addPiece(lastPos, null); // Capture the pawn en passant
        }
    }

    private static boolean isMoveInSet(Collection<ChessMove> moves, ChessMove move) {
		for (ChessMove chessMove : moves) if (move.equals(chessMove)) return true;
        return false;
    }

    private static void staticMakeMove(ChessGame game, ChessBoard board, ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (game.currentTeam != piece.getTeamColor()) throw new InvalidMoveException("Invalid move attempted");

        // see if move is in validMoves
        Collection<ChessMove> moves = staticValidMoves(game, board, move.getStartPosition());
        if (!isMoveInSet(moves, move)) throw new InvalidMoveException("Invalid move attempted");

        // Castling - Move ROOK according to move
        if (!piece.getBeenMoved() && piece.getPieceType() == ChessPiece.PieceType.KING) {
            ChessPosition pos = move.getEndPosition();
            int row = pos.getRow();
            ChessPosition queenSidePos = new ChessPosition(row, 3);
            ChessPosition kingSidePos = new ChessPosition(row, 7);
            if (pos.equals(queenSidePos)) {
                // Move ROOK to the right of KING
                movePiece(board, new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4), null));
            } else if (pos.equals(kingSidePos)) {
                // Move ROOK to the left of KING
                movePiece(board, new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6), null));
            }
        }

        // En Passant - If last move was opposing team's PAWN 2 forward && your PAWN next to them col:(-1, +1) && you move row(+1) THEN remove PAWN of opponent
        executeEnPassantMove(board, game.lastMove, move, piece);

        game.lastMove = move;
        game.currentTeam = (game.currentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        movePiece(board, move);
        piece.pieceMoved();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        staticMakeMove(this, this.board, move);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // if there are 0 validMoves for all team pieces
        for (int row = 1; row < 9; ++row) {
            for (int column = 1; column < 9; ++column) {
                ChessPosition pos = new ChessPosition(row, column);
                ChessPiece piece = this.board.getPiece(pos);
                if (piece == null) continue;
                if (piece.getTeamColor() != teamColor) continue;

                Collection<ChessMove> moves = validMoves(pos);
                if (!moves.isEmpty()) return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // if in check and all team pieces move and still in check
        if (!isInCheck(teamColor)) return false;
        return isInStalemate(teamColor);
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

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentTeam=" + currentTeam + "\n" +
                "board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.deepEquals(board, chessGame.board) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTeam);
    }
}
