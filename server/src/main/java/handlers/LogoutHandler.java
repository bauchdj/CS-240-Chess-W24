package handlers;

import spark.Spark;

import service.UserService;

import model.AuthData;

public class LogoutHandler {
	public static void logout(UserService userService) {
		Spark.delete("/session", (request, response) -> {
			String authToken = request.headers("authorization");

			CreateResponse.haltUnauthorized(authToken);

			boolean success = userService.logout(new AuthData(authToken));
			if (!success) CreateResponse.halt401();
			else CreateResponse.response200(response, "{}");

			return response.body();
		});
	}
}
