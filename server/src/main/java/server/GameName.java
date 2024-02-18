package server;

public class GameName {
	private final String gameName;

	public GameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameName() {
		return gameName;
	}

	@Override
	public String toString() {
		return "GameName{" +
				"gameName='" + gameName + '\'' +
				'}';
	}
}
