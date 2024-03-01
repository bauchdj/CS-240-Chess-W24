package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;

public class MySQLDatabase implements DataAccess {

	public void clearUsers() {

	}

	public void clearGames() {

	}

	public void clearAuth() {

	}

	public void clear() {

	}

	public void createUser(UserData user) {

	}

	public UserData getUser(String username) {
		return null;
	}

	public void createGame(GameData game) {

	}

	public GameData getGame(int gameId) {
		return null;
	}

	public HashSet<GameData> listGames() {
		return null;
	}

	public boolean userInGame(String username, int gameID, String clientColor) {
		return false;
	}

	public void updateGame(String username, int gameID, String clientColor) {

	}

	public void createAuth(AuthData authData) {

	}

	public AuthData getAuth(String authToken) {
		return null;
	}

	public void deleteAuth(String authToken) {

	}
}
