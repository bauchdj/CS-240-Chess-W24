package dataAccess;

import java.util.HashSet;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
	public void clearUsers();
	public void clearGames();
	public void clearAuth();

	// Clear Database (for testing or resetting purposes)
	public void clear();

	// User Operations
	public void createUser(UserData user);

	public UserData getUser(String username);

	// Game Operations
	public void createGame(GameData game);

	public GameData getGame(int gameId);

	public HashSet<GameData> listGames();

	public boolean userInGame(String username, int gameID, String clientColor);

	public boolean userExists(int gameID, String clientColor);

	public void updateUserInGame(String username, int gameID, String clientColor);

	public void updateGame(int gameID, GameData game);

	// Authentication Operations
	public void createAuth(AuthData authData);

	public AuthData getAuth(String authToken);

	public void deleteAuth(String authToken);
}
