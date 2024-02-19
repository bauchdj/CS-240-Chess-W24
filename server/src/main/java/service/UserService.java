package service;

import java.util.Objects;
import java.util.UUID;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
	private final UserDAO userDAO;
	private final AuthDAO authDAO;

	public UserService(UserDAO userDAO, AuthDAO authDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
	}

	private static AuthData createAuthData(String username) {
		String authToken = UUID.randomUUID().toString();;
		return new AuthData(username, authToken);
	}

	public AuthData register(UserData user) {
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();

		if (this.userDAO.userExists(username)
				|| username.isEmpty()
				|| password.isEmpty()
				|| email.isEmpty()) return null;

		UserData newUser = new UserData(username, password, email);
		this.userDAO.createUser(newUser);
		AuthData authData = createAuthData(username);
		this.authDAO.createAuth(authData);
		return authData;
	}

	private static boolean verifyPassword(String userPwd, String dbPwd) {
		return Objects.equals(userPwd, dbPwd);
	}

	public AuthData login(UserData user) {
		String username = user.getUsername();
		UserData dbUser = this.userDAO.getUser(username);

		if (dbUser != null && verifyPassword(user.getPassword(), dbUser.getPassword())) {
			AuthData authData = createAuthData(username);
			this.authDAO.createAuth(authData);
			return authData;
		}

		return null;
	}

	public boolean logout(AuthData authData) {
		if (!this.authDAO.authExists(authData)) return false;
		this.authDAO.deleteAuth(authData);
		return true;
	}
}
