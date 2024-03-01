package handlers;

public record UserJoinGame(String playerColor, int gameID) {
	@Override
	public String toString() {
		return "UserJoinGame{" +
				"playerColor='" + playerColor + '\'' +
				", gameID=" + gameID +
				'}';
	}
}
