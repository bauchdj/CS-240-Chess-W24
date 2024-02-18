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

			GameName gameName = new Gson().fromJson(request.body(), GameName.class);
			if (gameName == null || gameName.getGameName() == null || gameName.getGameName().trim().isEmpty()) {
				response.status(400);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: bad request"));
			}

			GameID gameID = gameService.createGame(new AuthData(authToken), gameName.getGameName());
			if (gameID == null) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			// [500] { "message": "Error: description" }

			response.status(200);
			response.type("application/json");
			return new Gson().toJson(gameID);
		});
	}
}
