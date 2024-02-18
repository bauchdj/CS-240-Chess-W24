package server;

import com.google.gson.Gson;
import model.AuthData;
import service.GameID;
import service.GameService;
import spark.Spark;

public class JoinHandler {
	public static void joinGame(GameService gameService) {
		Spark.put("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			if (authToken == null || authToken.isEmpty()) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			// Gson will throw a parsing error if int is empty request.body()
			UserJoinGame joinData = new Gson().fromJson(request.body(), UserJoinGame.class);
			if (joinData == null || joinData.getPlayerColor() == null ||
					joinData.getPlayerColor().trim().isEmpty() ||
					joinData.getGameID() == 0) {
				response.status(400);
				response.type("application/json");
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
				response.status(500);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: " + status));
			}

			response.status(200);
			response.type("application/json");
			return "{}";
		});
	}
}
