package server;

import spark.Spark;
import com.google.gson.Gson;

import service.UserService;

import model.UserData;
import model.AuthData;

public class LoginHandler {
	private LoginHandler() {};

	public static void login(UserService userService) {
		Spark.post("/session", (request, response) -> {
			UserData user = new Gson().fromJson(request.body(), UserData.class);
			AuthData authData = userService.login(user);

			if (authData == null) {
				response.status(401);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
			}

			// [500] { "message": "Error: description" }

			response.status(200);
			return new Gson().toJson(authData);
		});
	}
}
