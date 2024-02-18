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
		AuthData authData = new AuthData(username, authToken);
		return authData;
	}

	public AuthData register(UserData user) {
		String username = user.getUsername();
		UserData u = this.userDAO.getUser(username);
		if (u == null) {
			String password = user.getPassword();
			UserData newUser = new UserData(username, password, user.getEmail());
			this.userDAO.createUser(newUser);
			AuthData authData = createAuthData(username);
			this.authDAO.createAuth(authData);
			return authData;
		}

		return null;
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

	public void logout(AuthData authData) {
		this.authDAO.deleteAuth(authData);
	}
}
