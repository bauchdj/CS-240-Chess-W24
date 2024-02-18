package server;

import model.UserData;
import spark.Spark;
import com.google.gson.Gson;

import service.GameService;
import service.GameID;

import model.AuthData;

public class CreateHandler {
	public static void createGame(GameService gameService) {
		Spark.post("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			if (authToken == null || authToken.isEmpty()) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			String gameName = new Gson().fromJson(request.body(), GameName.class).getGameName();
			GameID gameID = gameService.createGame(new AuthData(authToken), gameName);
			if (gameID == null) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			response.status(200);
			response.type("application/json");
			return "{}";
		});
	}
}
