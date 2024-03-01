package handlers;

import spark.Spark;
import com.google.gson.Gson;

import service.GameService;

import model.AuthData;

public class JoinHandler {
	public static void joinGame(GameService gameService) {
		Spark.put("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			CreateResponse.haltUnauthorized(authToken);

			UserJoinGame joinData = new Gson().fromJson(request.body(), UserJoinGame.class);
			if (joinData == null) CreateResponse.halt400();
			else {
				String clientColor = joinData.playerColor();
				int gameID = joinData.gameID();
				String status = gameService.joinGame(new AuthData(authToken), clientColor, gameID);

				switch (status) {
					case "unauthorized" -> CreateResponse.halt401();
					case "already taken" -> CreateResponse.halt403();
					case "game does not exist" -> CreateResponse.halt400Msg("Error: " + status);
					default -> CreateResponse.response200(response, "{}");
				}
			}

			return response.body();
		});
	}
}
