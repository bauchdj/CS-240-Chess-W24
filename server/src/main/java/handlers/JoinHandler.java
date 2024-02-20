package handlers;

import com.google.gson.Gson;
import server.UserJoinGame;
import spark.Spark;

import service.GameService;

import model.AuthData;

public class JoinHandler {
	public static void joinGame(GameService gameService) {
		Spark.put("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			if (authToken == null || authToken.isEmpty()) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			UserJoinGame joinData = new Gson().fromJson(request.body(), UserJoinGame.class);

			if (joinData == null) {
				response.status(400);
				response.type("application/json");
				System.out.println(request.body());
				return new Gson().toJson(new ErrorResponse("Error: bad request"));
			}

			String clientColor = joinData.getPlayerColor();
			int gameID = joinData.getGameID();
			String status = gameService.joinGame(new AuthData(authToken), clientColor, gameID);

			if (status.equals("unauthorized")) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			} else if (status.equals("already taken")) {
				response.status(403);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: already taken"));
			} else if (status.equals("game does not exist")) {
				response.status(400);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: " + status));
			}

			// [500] { "message": "Error: description" }

			response.status(200);
			response.type("application/json");
			return "{}";
		});
	}
}
