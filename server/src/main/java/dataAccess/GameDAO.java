package dataAccess;

import model.GameData;

import java.util.List;

public class GameDAO {
	private final Database database;

	public GameDAO(Database database) {
		this.database = database;
	}

	public void clearGames() {
		this.database.clearGames();
	}

	/**
	 * Adds a new game to the database.
	 *
	 * @param game The game to add.
	 */
	public void createGame(GameData game) {
		this.database.createGame(game);
	}

	/**
	 * Retrieves a game by its ID.
	 *
	 * @param gameID The ID of the game to retrieve.
	 * @return An GameData if found, or null.
	 */
	public GameData getGame(int gameID) {
		return this.database.getGame(gameID);
	}

	public boolean gameExist(int gameID) {
		return getGame(gameID) != null;
	}

	/**
	 * Lists all games stored in the database.
	 *
	 * @return A List of all games.
	 */
	public List<GameData> listGames() {
		return database.listGames();
	}

	public boolean userExists(String username, int gameId, String clientColor) {
		return this.database.userInGame(username, gameId, clientColor);
	}

	/**
	 * Updates the game participants for a given game ID.
	 *
	 * @param username The username of the participant.
	 * @param gameId The ID of the game to be updated.
	 * @param clientColor The color chosen by the participant (e.g., "white" or "black").
	 */
	public void updateGame(String username, int gameId, String clientColor) {
		this.database.updateGame(username, gameId, clientColor);
	}
}
