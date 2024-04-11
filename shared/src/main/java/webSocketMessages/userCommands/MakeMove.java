package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import webSocketMessages.userCommands.UserGameCommand;

public class MakeMove extends UserGameCommand {
	private ChessGame.TeamColor playerColor;
	private ChessMove move;

	public MakeMove(int gameID, ChessMove move, String authToken) {
		super(authToken);
		this.commandType = CommandType.MAKE_MOVE;
		this.gameID = gameID;
		this.playerColor = playerColor;
		this.move = move;
	}

	public int getGameID() {
		return gameID;
	}

	public ChessGame.TeamColor getPlayerColor() {
		return playerColor;
	}

	public ChessMove getMove() {
		return move;
	}
}
