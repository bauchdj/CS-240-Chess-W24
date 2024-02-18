package service;

public class GameID {
	private final int gameID;

	public GameID(int gameID) {
		this.gameID = gameID;
	}

	public int getGameID() {
		return gameID;
	}

	@Override
	public String toString() {
		return "GameID{" +
				"gameID=" + gameID +
				'}';
	}
}
