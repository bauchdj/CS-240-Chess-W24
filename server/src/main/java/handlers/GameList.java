package handlers;

import model.GameData;

import java.util.HashSet;

public record GameList(HashSet<GameData> games) {
	private static final String game = "games";

	@Override
	public String toString() {
		return "GameList{" +
				"games=" + games +
				'}';
	}
}
