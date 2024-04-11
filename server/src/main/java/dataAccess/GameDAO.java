package dataAccess;

import model.GameData;

import java.util.HashSet;

public class GameDAO {
	private final DataAccess database;

	public GameDAO(DataAccess database) {
		this.database = database;
	}

	public boolean clearGames() {
		this.database.clearGames();
		return true;
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
	public HashSet<GameData> listGames() {
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
	public void updateUserInGame(String username, int gameId, String clientColor) {
		this.database.updateUserInGame(username, gameId, clientColor);
	}

	public void updateGame(int gameID, GameData game) {
		this.database.updateGame(gameID, game);
	}
}
