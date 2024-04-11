package ui;

import chess.ChessGame;
import connection.WebSocketConnection;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessageError;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;

public class GamePlayUI extends Repl {
	private enum MenuOption {
		HELP(1, "Help"),
		REDRAW_CHESS_BOARD(2, "Redraw Chess Board"),
		MAKE_MOVE(3, "Make Move"),
		RESIGN(4, "Resign"),
		HIGHLIGHT_LEGAL_MOVES(5, "Highlight Legal Moves"),
		LEAVE(6, "Leave");

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
		WebSocketConnection ws = new WebSocketConnection(this, authToken);
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
		drawChessboard();
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
				case RESIGN -> resign();
				case HIGHLIGHT_LEGAL_MOVES -> highlightLegalMoves();
				case LEAVE -> leave();
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
		System.out.println("4. Resign:");
		System.out.println("   - Allows you to resign from the game, forfeiting the match.");
		System.out.println("   - You will be prompted to confirm your decision.");
		System.out.println("5. Highlight Legal Moves:");
		System.out.println("   - Highlights the legal moves for a selected piece.");
		System.out.println("   - Enter the coordinates of the piece (e.g., 'e2') to see its legal moves.");
		System.out.println("   - This is a local operation and does not affect other players' views.");
		System.out.println("6. Leave:");
		System.out.println("   - Allows you to leave the game, whether you are playing or observing.");
		System.out.println("   - You will be returned to the Post-Login UI.");
	}

	public void handleLoadGame(LoadGame loadGame) {
		// Handle load game logic
		// Update the game state on the client-side
	}

	public void handleError(ServerMessageError error) {
		// Handle error logic
		// Display the error message to the user
	}

	public void handleNotification(Notification notification) {
		// Handle notification logic
		// Display the notification message to the user based on the event type
	}

	private void makeMove() {
		System.out.print("Enter your move (e.g., e2e4): ");
		String move = scanner.nextLine();
		// TODO: Implement the logic to make the move on the chessboard
		// Update the chessboard on all clients involved in the game
	}

	private void resign() {
		System.out.print("Are you sure you want to resign? (y/n): ");
		String confirmation = scanner.nextLine();
		if (confirmation.equalsIgnoreCase("y")) {
			// TODO: Implement the logic to resign the game
			System.out.println("You have resigned from the game.");
		}
	}

	private void highlightLegalMoves() {
		System.out.print("Enter the coordinates of the piece (e.g., e2): ");
		String coordinates = scanner.nextLine();
		// TODO: Implement the logic to highlight legal moves for the selected piece
		// This is a local operation and has no effect on remote users' screens
	}

	private void redrawChessboard() { return; }

	private void leave() {
		WebSocketConnection ws = app.getConnection();
		ws.close();
		app.setGameID(0);
		app.setColor(null);

		navigate();
		app.navigateToPostLogin();
	}

	private void drawChessboard() {
		boolean whiteAtBottom = (app.getColor() != ChessGame.TeamColor.BLACK);
		drawChessboardOrientation(whiteAtBottom);
	}

	private void drawChessboardOrientation(boolean whiteAtBottom) {
		String[][] board = getInitialChessboard();

		String columns = generateColumnLabels(whiteAtBottom);

		System.out.println(columns);

		drawChessboardRows(board, whiteAtBottom);

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

	private void drawChessboardRows(String[][] board, boolean whiteAtBottom) {
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

	private void drawChessboardRow(String[][] board, int row, boolean whiteAtBottom) {
		int colStart = whiteAtBottom ? 0 : 7;
		int colEnd = whiteAtBottom ? 8 : -1;
		int colStep = whiteAtBottom ? 1 : -1;
		for (int col = colStart; col != colEnd; col += colStep) {
			String piece = board[row][col];
			String bgColor = ((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
			System.out.print(bgColor + piece + EscapeSequences.RESET_BG_COLOR);
		}
	}

	private String[][] getInitialChessboard() {
		String[][] board = new String[8][8];

		// Initialize the chessboard with empty squares
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board[row][col] = EscapeSequences.EMPTY;
			}
		}

		// Place the white pieces
		board[7][0] = EscapeSequences.WHITE_ROOK;
		board[7][1] = EscapeSequences.WHITE_KNIGHT;
		board[7][2] = EscapeSequences.WHITE_BISHOP;
		board[7][3] = EscapeSequences.WHITE_KING;
		board[7][4] = EscapeSequences.WHITE_QUEEN;
		board[7][5] = EscapeSequences.WHITE_BISHOP;
		board[7][6] = EscapeSequences.WHITE_KNIGHT;
		board[7][7] = EscapeSequences.WHITE_ROOK;
		for (int col = 0; col < 8; col++) {
			board[6][col] = EscapeSequences.WHITE_PAWN;
		}

		// Place the black pieces
		board[0][0] = EscapeSequences.BLACK_ROOK;
		board[0][1] = EscapeSequences.BLACK_KNIGHT;
		board[0][2] = EscapeSequences.BLACK_BISHOP;
		board[0][3] = EscapeSequences.BLACK_KING;
		board[0][4] = EscapeSequences.BLACK_QUEEN;
		board[0][5] = EscapeSequences.BLACK_BISHOP;
		board[0][6] = EscapeSequences.BLACK_KNIGHT;
		board[0][7] = EscapeSequences.BLACK_ROOK;
		for (int col = 0; col < 8; col++) {
			board[1][col] = EscapeSequences.BLACK_PAWN;
		}

		return board;
	}
}