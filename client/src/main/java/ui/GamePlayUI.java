package ui;

import chess.*;
import connection.WebSocketConnection;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.Collection;
import java.util.HashSet;

public class GamePlayUI extends Repl {
	private WebSocketConnection ws;
	private ChessGame game;
	private HashSet<ChessPosition> validMovePositions = null;

	private enum MenuOption {
		HELP(1, "Help"),
		REDRAW_CHESS_BOARD(2, "Redraw Chess Board"),
		MAKE_MOVE(3, "Make Move"),
		HIGHLIGHT_LEGAL_MOVES(4, "Highlight Legal Moves"),
		LEAVE(5, "Leave"),
		RESIGN(6, "Resign");

		private final int number;
		private final String description;

		MenuOption(int number, String description) {
			this.number = number;
			this.description = description;
		}

		public int getNumber() {
			return number;
		}

		public String getDescription() {
			return description;
		}
	}

	public GamePlayUI(App app) {
		super(app);
	}

	@Override
	protected void onStart() {
		String authToken = app.getAuthToken();
		ws = new WebSocketConnection(this, authToken);
		app.setConnection(ws);
		ws.connect();

		Object join = (app.getColor() != null) ?
			new JoinPlayer(app.getGameID(), app.getColor(), authToken) :
			new JoinObserver(app.getGameID(), authToken);

		String message = gson.toJson(join);
		ws.sendMessage(message);

		System.out.println("On Start only running once... RIGHT!!!");
	}

	@Override
	protected void displayPrompt() {
		System.out.println("Chessboard:");
		if (game != null) drawChessboard();
		for (GamePlayUI.MenuOption option : GamePlayUI.MenuOption.values()) {
			System.out.println(option.getNumber() + ". " + option.getDescription());
		}
	}

	@Override
	protected void processInput(String input) {
		try {
			int choice = Integer.parseInt(input);
			GamePlayUI.MenuOption selectedOption = GamePlayUI.MenuOption.values()[choice - 1];
			switch (selectedOption) {
				case HELP -> displayHelp();
				case REDRAW_CHESS_BOARD -> redrawChessboard();
				case MAKE_MOVE -> makeMove();
				case HIGHLIGHT_LEGAL_MOVES -> highlightLegalMoves();
				case LEAVE -> leave();
				case RESIGN -> resign();
			}
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid choice. Please try again.");
		}
	}

	@Override
	protected void displayHelp() {
		System.out.println("Gameplay Help:");
		System.out.println("1. Help:");
		System.out.println("   - Displays this help menu, providing information about the available actions.");
		System.out.println("2. Redraw Chess Board:");
		System.out.println("   - Redraws the chessboard, updating its current state.");
		System.out.println("3. Make Move:");
		System.out.println("   - Allows you to make a move by entering the source and destination coordinates.");
		System.out.println("   - Example: To move a piece from e2 to e4, enter 'e2e4'.");
		System.out.println("4. Highlight Legal Moves:");
		System.out.println("   - Highlights the legal moves for a selected piece.");
		System.out.println("   - Enter the coordinates of the piece (e.g., 'e2') to see its legal moves.");
		System.out.println("   - This is a local operation and does not affect other players' views.");
		System.out.println("5. Leave:");
		System.out.println("   - Allows you to leave the game, whether you are playing or observing.");
		System.out.println("   - You will be returned to the Post-Login UI.");
		System.out.println("6. Resign:");
		System.out.println("   - Allows you to resign from the game, forfeiting the match.");
		System.out.println("   - You will be prompted to confirm your decision.");
	}

	public void handleLoadGame(LoadGame loadGame) {
		game = loadGame.getGame();
		System.out.println("ChessBoard loaded. Redraw Chessboard by entering 2");
	}

	public void handleError(ServerMessageError error) {
		System.out.println(error.getErrorMessage());
	}

	public void handleNotification(Notification notification) {
		System.out.println(notification.getMessage());

		String message = notification.getMessage();
		if (message.contains("Game Over")) {
			if (message.contains("Checkmate: true")) {
				System.out.println("In Checkmate!!!");
			} else if (message.contains("Stalemate: true")) {
				System.out.println("In Stalemate!!!");
			}

			System.out.println("You can now leave or resign from the game.");
		}
	}

	private void makeMove() {
		System.out.print("Enter your move (e.g., e2e4): ");
		String move = scanner.nextLine();

		ChessPosition startPos = new ChessPosition(move.substring(0, 2));
		ChessPosition endPos = new ChessPosition(move.substring(2));

		ChessMove chessMove = new ChessMove(startPos, endPos, null);
		MakeMove makeMove = new MakeMove(app.getGameID(), chessMove, app.getAuthToken());
		ws.sendMessage(gson.toJson(makeMove));
	}

	private void highlightLegalMoves() {
		System.out.print("Enter the coordinates of the piece (e.g., e2): ");
		String coordinates = scanner.nextLine();

		// TODO: Implement the logic to highlight legal moves for the selected piece
		// This is a local operation and has no effect on remote users' screens

		ChessPosition position = new ChessPosition(coordinates);

		if (game.getBoard().getPiece(position) == null) {
			System.out.println("Sorry, no piece there.");
			return;
		}

		Collection<ChessMove> moves = game.validMoves(position);

		// Create a list to store the valid move positions
		validMovePositions = new HashSet<>();

		// Extract the end positions from the valid moves and add them to the list
		for (ChessMove move : moves) {
			validMovePositions.add(move.getEndPosition());
		}

		// Draw the chessboard with the valid move positions highlighted
		drawChessboard();
	}

	private void leave() {
		ws.sendMessage(gson.toJson(new Leave(app.getGameID(), app.getAuthToken())));

		navigate();
		app.navigateToPostLogin();
	}
	private void resign() {
		System.out.print("Are you sure you want to resign? (y/n): ");
		String confirmation = scanner.nextLine();
		if (confirmation.equalsIgnoreCase("y")) {
			System.out.println("You have resigned from the game.");

			ws.sendMessage(gson.toJson(new Resign(app.getGameID(), app.getAuthToken())));

			ws.close();
			app.setGameID(0);
			app.setColor(null);

			navigate();
			app.navigateToPostLogin();
		}
	}

	private void redrawChessboard() { return; }

	private void drawChessboard() {
		drawChessboardOrientation();
	}

	private void drawChessboardOrientation() {
		boolean whiteAtBottom = (app.getColor() == ChessGame.TeamColor.WHITE);
		System.out.println(whiteAtBottom);

		String columns = generateColumnLabels(whiteAtBottom);

		System.out.println(columns);

		drawChessboardRows(game.getBoard(), whiteAtBottom);

		System.out.println(columns);
	}

	private String generateColumnLabels(boolean whiteAtBottom) {
		StringBuilder columnsBuilder = new StringBuilder("   ");
		char colStartLabel = whiteAtBottom ? 'a' : 'h';
		char colEndLabel = whiteAtBottom ? 'h' : 'a';
		int colLabelStep = whiteAtBottom ? 1 : -1;
		for (char col = colStartLabel; col != colEndLabel + colLabelStep; col += colLabelStep) {
			columnsBuilder.append("\u2003").append(col).append("\u2003");
		}
		return columnsBuilder.toString();
	}

	private void drawChessboardRows(ChessBoard board, boolean whiteAtBottom) {
		int rowStart = whiteAtBottom ? 7 : 0;
		int rowEnd = whiteAtBottom ? -1 : 8;
		int rowStep = whiteAtBottom ? -1 : 1;
		for (int row = rowStart; row != rowEnd; row += rowStep) {
			String displayRow = "\u2003" + (row + 1) + "\u2003";
			System.out.print(displayRow);
			drawChessboardRow(board, row, whiteAtBottom);
			System.out.println(displayRow);
		}
	}

	private void drawChessboardRow(ChessBoard board, int row, boolean whiteAtBottom) {
		int colStart = whiteAtBottom ? 0 : 7;
		int colEnd = whiteAtBottom ? 8 : -1;
		int colStep = whiteAtBottom ? 1 : -1;
		for (int col = colStart; col != colEnd; col += colStep) {
			ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
			String pieceSymbol = getPieceSymbol(piece);

			// Check if the current position is a valid move position, if so make it Green!
			String bgColor = (validMovePositions != null && validMovePositions.contains(new ChessPosition(row + 1, col + 1))) ?
				((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN :
				((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

			System.out.print(bgColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
		}
	}

	private String getPieceSymbol(ChessPiece piece) {
		if (piece == null) {
			return EscapeSequences.EMPTY;
		}

		ChessGame.TeamColor color = piece.getTeamColor();
		ChessPiece.PieceType type = piece.getPieceType();

		switch (type) {
			case KING:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
			case QUEEN:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
			case BISHOP:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
			case KNIGHT:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
			case ROOK:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
			case PAWN:
				return color == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
			default:
				return EscapeSequences.EMPTY;
		}
	}
}