package server;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import spark.Spark;

public class clearHandler {
	private clearHandler() {}

	public static void clear(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
		Spark.delete("/db", (request, response) -> {
			userDAO.clearUsers();
			gameDAO.clearGames();
			authDAO.clearAuth();

			// [500] { "message": "Error: description" }

			response.status(200);
			response.type("application/json");
			return "{}";
		});
	}
}
