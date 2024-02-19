package server;

import model.GameData;

import java.util.List;

public class GameList {
	private static final String game = "games";
	private final List<GameData> games;

	public GameList(List<GameData> games) {
		this.games = games;
	}

	public List<GameData> getGames() {
		return games;
	}
}
