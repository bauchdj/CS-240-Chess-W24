package messages;

import webSocketMessages.userCommands.UserGameCommand;

public class Resign extends UserGameCommand {

	public Resign(int gameID, String authToken) {
		super(authToken);
		this.commandType = CommandType.RESIGN;
		this.gameID = gameID;
	}

	public int getGameID() {
		return gameID;
	}
}
