package dataAccess;

import model.UserData;

public class UserDAO {
	private final DataAccess database;

	public UserDAO(DataAccess database) {
		this.database = database;
	}

	public boolean clearUsers() {
		this.database.clearUsers();
		return true;
	}

	/**
	 * Adds a new user to the database.
	 *
	 * @param user The user to add.
	 */
	public void createUser(UserData user) {
		this.database.createUser(user);
	}

	/**
	 * Retrieves a user by username.
	 *
	 * @param username The username of the user to retrieve.
	 * @return An UserData if found, or null.
	 */
	public UserData getUser(String username) {
		return this.database.getUser(username);
	}

	/**
	 * Checks if a user exists by username.
	 *
	 * @param username The username to check.
	 * @return true if the user exists, false otherwise.
	 */
	public boolean userExists(String username) {
		return getUser(username) != null;
	}
}
