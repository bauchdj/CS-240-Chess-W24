package webSocketMessages.userCommands;

import webSocketMessages.userCommands.UserGameCommand;

public class Leave extends UserGameCommand {

	public Leave(int gameID, String authToken) {
		super(authToken);
		this.commandType = CommandType.LEAVE;
		this.gameID = gameID;
	}

	public int getGameID() {
		return gameID;
	}
}
