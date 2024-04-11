package messages;

import webSocketMessages.userCommands.UserGameCommand;

public class JoinObserver extends UserGameCommand {

	public JoinObserver(int gameID, String authToken) {
		super(authToken);
		this.commandType = CommandType.JOIN_OBSERVER;
		this.gameID = gameID;
	}

	public int getGameID() {
		return gameID;
	}
}
