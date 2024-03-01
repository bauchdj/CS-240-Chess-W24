package service;

public record GameID(int gameID) {
	@Override
	public String toString() {
		return "GameID{" +
			   "gameID=" + gameID +
			   '}';
	}
}
