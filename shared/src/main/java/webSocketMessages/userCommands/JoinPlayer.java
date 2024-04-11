package messages;

import chess.ChessGame;
import webSocketMessages.userCommands.UserGameCommand;

public class JoinPlayer extends UserGameCommand {
	private ChessGame.TeamColor playerColor;

	public JoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken) {
		super(authToken);
		this.commandType = CommandType.JOIN_PLAYER;
		this.gameID = gameID;
		this.playerColor = playerColor;
	}

	public int getGameID() {
		return gameID;
	}

	public ChessGame.TeamColor getPlayerColor() {
		return playerColor;
	}
}