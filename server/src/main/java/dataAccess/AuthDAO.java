package dataAccess;

import model.AuthData;

import java.util.Optional;

public class AuthDAO {
	private final Database database;

	public AuthDAO(Database database) {
		this.database = database;
	}

	/**
	 * Adds a new authentication data entry to the database.
	 *
	 * @param authData The authentication data to add.
	 */
	public void createAuth(AuthData authData) {
		database.createAuth(authData);
	}

	/**
	 * Retrieves authentication data by auth token.
	 *
	 * @param authToken The authentication token.
	 * @return An Optional containing the AuthData if found, or an empty Optional if not found.
	 */
	public AuthData getAuth(String authToken) {
		return database.getAuth(authToken);
	}

	/**
	 * Deletes an authentication data entry based on the auth token.
	 *
	 * @param authData The authentication token to delete.
	 */
	public void deleteAuth(AuthData authData) {
		database.deleteAuth(authData.getAuthToken());
	}
}
