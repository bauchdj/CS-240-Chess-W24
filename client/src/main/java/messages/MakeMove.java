package messages;

import chess.ChessMove;
import webSocketMessages.userCommands.UserGameCommand;

public class MakeMove extends UserGameCommand {
	private ChessMove move;

	public MakeMove(int gameID, ChessMove move, String authToken) {
		super(authToken);
		this.commandType = CommandType.MAKE_MOVE;
		this.gameID = gameID;
		this.move = move;
	}

	public int getGameID() {
		return gameID;
	}

	public ChessMove getMove() {
		return move;
	}
}
