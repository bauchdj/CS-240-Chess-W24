package server;

public class UserJoinGame {
	private final String playerColor;
	private final int gameID;

	public UserJoinGame(String playerColor, int gameID) {
		this.playerColor = playerColor;
		this.gameID = gameID;
	}

	public String getPlayerColor() {
		return playerColor;
	}

	public int getGameID() {
		return gameID;
	}
}
