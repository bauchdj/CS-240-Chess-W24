package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
	private final HashSet<UserData> users = new HashSet<>();
	private final HashSet<GameData> games = new HashSet<>();
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
		users.add(user);
	}

	public UserData getUser(String username) {
		return users.stream()
				.filter(user -> user.getUsername()
				.equals(username))
				.findFirst().orElse(null);
	}

	// Game Operations
	public void createGame(GameData game) { games.add(game); }

	public GameData getGame(int gameId) {
		return games.stream()
				.filter(game -> game.getGameID() == gameId)
				.findFirst()
				.orElse(null);
	}

	public HashSet<GameData> listGames() {
		return new HashSet<>(games);
	}

	public boolean userInGame(String username, int gameID, String clientColor) {
		GameData game = getGame(gameID);
		return game != null &&
				(("white".equalsIgnoreCase(clientColor) && game.getWhiteUsername() != null) ||
				 "black".equalsIgnoreCase(clientColor) && game.getBlackUsername() != null);
	}

	public void updateGame(String username, int gameID, String clientColor) {
		GameData game = getGame(gameID);
		if (game != null) {
			if ("white".equalsIgnoreCase(clientColor)) {
				game.setWhiteUsername(username);
			} else if ("black".equalsIgnoreCase(clientColor)) {
				game.setBlackUsername(username);
			}
		};
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

