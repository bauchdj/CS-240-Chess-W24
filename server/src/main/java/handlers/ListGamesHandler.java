package handlers;

import spark.Spark;

import service.GameService;

import model.AuthData;

public class ListGamesHandler {
	public static void listGames(GameService gamerService) {
		Spark.get("/game", (request, response) -> {
			String authToken = request.headers("authorization");

			CreateResponse.haltUnauthorized(authToken);

			GameList games = new GameList(gamerService.listGames(new AuthData(authToken)));
			if (games.getGames() == null) CreateResponse.halt401();
			else CreateResponse.response200(response, games);

			return response.body();
		});
	}
}
