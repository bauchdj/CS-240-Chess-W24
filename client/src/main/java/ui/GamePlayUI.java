package ui;

import connection.WebSocketConnection;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

public class GamePlayUI extends Repl {
	private enum MenuOption {
		LEAVE_GAME(1, "Leave Game");

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
				case LEAVE_GAME -> leaveGame();
			}
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid choice. Please try again.");
		}
	}

	@Override
	protected void displayHelp() {
		System.out.println("Gameplay Help:");
		System.out.println("- The chessboard is displayed in two orientations.");
		System.out.println("- White pieces are shown at the bottom in the first orientation.");
		System.out.println("- Black pieces are shown at the bottom in the second orientation.");
		System.out.println("- Gameplay functionality will be implemented later.");
	}

	private void leaveGame() {
		WebSocketConnection ws = app.getConnection();
		ws.close();
		app.setPlaying(false);

		navigate();
		app.navigateToPostLogin();
	}

	private void drawChessboard() {
		// Draw the chessboard with black pieces at the bottom
		drawChessboardOrientation(false);

		System.out.println();

		// Draw the chessboard with white pieces at the bottom
		drawChessboardOrientation(true);
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