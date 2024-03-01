package handlers;

import spark.Spark;
import com.google.gson.Gson;

import service.GameService;
import service.GameID;

import model.AuthData;

public class CreateHandler {
	public static void createGame(GameService gameService) {
		Spark.post("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			CreateResponse.haltUnauthorized(authToken);

			GameName gameName = new Gson().fromJson(request.body(), GameName.class);
			if (gameName == null || gameName.gameName() == null || gameName.gameName().trim().isEmpty()) {
				CreateResponse.halt400();
			} else {
				GameID gameID = gameService.createGame(new AuthData(authToken), gameName.gameName());
				if (gameID == null) CreateResponse.halt401();
				else CreateResponse.response200(response, gameID);
			}

			return response.body();
		});
	}
}
