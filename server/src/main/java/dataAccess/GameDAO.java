package dataAccess;

import chess.ChessGame;
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

	public boolean userInGame(String username, int gameId, String clientColor) {
		return this.database.userInGame(username, gameId, clientColor);
	}

	public boolean userExists(int gameId, String clientColor) {
		return this.database.userExists(gameId, clientColor);
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

	/**
	 * Updates the game data for a given game ID.
	 *
	 * @param gameID The ID of the game to be updated.
	 * @param game The game data to be inserted.
	 */
	public void updateGame(int gameID, GameData game) {
		this.database.updateGame(gameID, game);
	}

	public void removeUserFromGame(String username, int gameID) {
		String clientColor = (userInGame(username, gameID, "white")) ? "white" : "black";
		updateUserInGame(null, gameID, clientColor);
	}

	public ChessGame.TeamColor getUserColorInGame(String username, int gameID) {
		return (userInGame(username, gameID, "white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
	}

	public boolean userIsPlayer(String username, int gameID) {
		return userInGame(username, gameID, "white") || userInGame(username, gameID, "black");
	}
}
