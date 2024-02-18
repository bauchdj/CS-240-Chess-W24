package dataAccess;

import model.UserData;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UserDAO {
	private final Database database;

	public UserDAO(Database database) {
		this.database = database;
	}

	public void clearUsers() {
		database.clearUsers();
	}

	/**
	 * Adds a new user to the database.
	 *
	 * @param user The user to add.
	 */
	public void createUser(UserData user) {
		database.createUser(user);
	}

	/**
	 * Retrieves a user by username.
	 *
	 * @param username The username of the user to retrieve.
	 * @return An Optional containing the User if found, or an empty Optional if not found.
	 */
	public UserData getUser(String username) {
		return database.getUser(username);
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
