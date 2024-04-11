package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Database implements DataAccess {
	private final Map<String, UserData> users = new HashMap<>();
	private final Map<Integer, GameData> games = new HashMap<>();
	private final Map<String, AuthData> authTokens = new HashMap<>();

	public void clearUsers() { users.clear(); }
	public void clearGames() { games.clear(); }
	public void clearAuth() { authTokens.clear(); }

	// Clear Database (for testing or resetting purposes)
	public void clear() {
		clearUsers();
		clearGames();
		clearAuth();
	}

	// User Operations
	public void createUser(UserData user) {
		users.put(user.getUsername(), user);
	}

	public UserData getUser(String username) {
		return users.get(username);
	}

	// Game Operations
	public void createGame(GameData game) { games.put(game.getGameID(), game); }

	public GameData getGame(int gameId) {
		return games.get(gameId);
	}

	public void removeGame(int gameID) {
		games.remove(gameID);
	}

	public HashSet<GameData> listGames() {
		return new HashSet<>(games.values());
	}

	public boolean userInGame(String username, int gameID, String clientColor) {
		GameData game = getGame(gameID);
		return game != null &&
				(("white".equalsIgnoreCase(clientColor) && game.getWhiteUsername() != null && game.getWhiteUsername().equals(username)) ||
				 "black".equalsIgnoreCase(clientColor) && game.getBlackUsername() != null && game.getBlackUsername().equals(username));
	}

	public boolean userExists(int gameID, String clientColor) {
		GameData game = getGame(gameID);
		return game != null &&
			(("white".equalsIgnoreCase(clientColor) && game.getWhiteUsername() != null) ||
				"black".equalsIgnoreCase(clientColor) && game.getBlackUsername() != null);
	}

	public void updateUserInGame(String username, int gameID, String clientColor) {
		GameData game = getGame(gameID);
		if (game != null) {
			if ("white".equalsIgnoreCase(clientColor)) {
				game.setWhiteUsername(username);
			} else if ("black".equalsIgnoreCase(clientColor)) {
				game.setBlackUsername(username);
			}
		};
	}

	public void updateGame(int gameID, GameData game) {
		games.put(gameID, game);
	}

	// Authentication Operations
	public void createAuth(AuthData authData) {
		authTokens.put(authData.getAuthToken(), authData);
	}

	public AuthData getAuth(String authToken) {
		return authTokens.get(authToken);
	}

	public void deleteAuth(String authToken) {
		authTokens.remove(authToken);
	}
}

