package server.handlers;

import com.google.gson.Gson;
import model.AuthData;
import server.ErrorResponse;
import spark.Spark;

import service.GameService;

public class ListGamesHandler {
	public static void listGames(GameService gamerService) {
		Spark.get("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			if (authToken == null || authToken.isEmpty()) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			GameList games = new GameList(gamerService.listGames(new AuthData(authToken)));

			if (games.getGames() == null) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			// [500] { "message": "Error: description" }

			response.status(200);
			response.type("application/json");
			return new Gson().toJson(games);
		});
	}
}
