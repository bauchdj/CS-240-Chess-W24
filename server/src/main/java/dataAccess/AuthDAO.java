package dataAccess;

import model.AuthData;

public class AuthDAO {
	private final Database database;

	public AuthDAO(Database database) {
		this.database = database;
	}

	public void clearAuth() {
		this.database.clearAuth();
	}

	/**
	 * Adds a new authentication data entry to the database.
	 *
	 * @param authData The authentication data to add.
	 */
	public void createAuth(AuthData authData) {
		this.database.createAuth(authData);
	}

	/**
	 * Retrieves authentication data by auth token.
	 *
	 * @param authData The authentication token.
	 * @return An AuthData if found, or null.
	 */
	public AuthData getAuth(AuthData authData) {
		return this.database.getAuth(authData.getAuthToken());
	}

	/**
	 * Checks if a authToken exists by authData.
	 *
	 * @param authData The username to check.
	 * @return true if the authToken exists, false otherwise.
	 */
	public boolean authExists(AuthData authData) {
		return getAuth(authData) != null;
	}

	/**
	 * Deletes an authentication data entry based on the auth token.
	 *
	 * @param authData The authentication token to delete.
	 */
	public void deleteAuth(AuthData authData) {
		this.database.deleteAuth(authData.getAuthToken());
	}
}
