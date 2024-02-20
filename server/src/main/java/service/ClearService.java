package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.GameDAO;

public class ClearService {
	private final AuthDAO authDAO;
	private final UserDAO userDAO;
	private final GameDAO gameDAO;

	public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.userDAO = userDAO;
		this.gameDAO = gameDAO;
	}

	public boolean clear() {
		if (!userDAO.clearUsers()) return false;
		if (!gameDAO.clearGames()) return false;
		if (!authDAO.clearAuth()) return false;

		return true;
	}
}
