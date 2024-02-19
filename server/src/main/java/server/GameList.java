package server;

import model.GameData;

import java.util.HashSet;

public class GameList {
	private static final String game = "games";
	private final HashSet<GameData> games;

	public GameList(HashSet<GameData> games) {
		this.games = games;
	}

	public HashSet<GameData> getGames() {
		return games;
	}

	@Override
	public String toString() {
		return "GameList{" +
				"games=" + games +
				'}';
	}
}
