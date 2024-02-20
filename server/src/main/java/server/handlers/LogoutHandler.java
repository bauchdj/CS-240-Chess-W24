package server.handlers;

import server.ErrorResponse;
import spark.Spark;
import com.google.gson.Gson;

import service.UserService;

import model.UserData;
import model.AuthData;

public class LogoutHandler {
	public static void logout(UserService userService) {
		Spark.delete("/session", (request, response) -> {
			String authToken = request.headers("authorization");

			if (authToken == null || authToken.isEmpty()) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			boolean success = userService.logout(new AuthData(authToken));
			if (!success) {
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
