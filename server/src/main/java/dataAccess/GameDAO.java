package dataAccess;

import model.GameData;

import java.util.List;
import java.util.Optional;

public class GameDAO {
	private final Database database;

	public GameDAO(Database database) {
		this.database = database;
	}

	/**
	 * Adds a new game to the database.
	 *
	 * @param game The game to add.
	 */
	public void createGame(GameData game) {
		database.createGame(game);
	}

	/**
	 * Retrieves a game by its ID.
	 *
	 * @param gameId The ID of the game to retrieve.
	 * @return An Optional containing the Game if found, or an empty Optional if not found.
	 */
	public GameData getGameById(int gameId) {
		return database.getGame(gameId);
	}

	/**
	 * Lists all games stored in the database.
	 *
	 * @return A List of all games.
	 */
	public List<GameData> getAllGames() {
		return database.listGames();
	}

	/**
	 * Updates the game participants for a given game ID.
	 *
	 * @param username The username of the participant.
	 * @param gameId The ID of the game to be updated.
	 * @param clientColor The color chosen by the participant (e.g., "white" or "black").
	 */
	public void updateGame(String username, int gameId, String clientColor) {
		database.updateGame(username, gameId, clientColor);
	}
}
