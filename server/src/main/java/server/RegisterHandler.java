package server;

import spark.Spark;
import com.google.gson.Gson;

import service.UserService;

import model.UserData;
import model.AuthData;

public class RegisterHandler {
	public static void register(UserService userService) {
		Spark.post("/user", (request, response) -> {
			UserData user = new Gson().fromJson(request.body(), UserData.class);

			if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
					user.getPassword() == null || user.getPassword().trim().isEmpty()) {
				response.status(400);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: bad request"));
			}

			AuthData authData = userService.register(user);
			if (authData == null) {
				response.status(403);
				response.type("application/json");
				return new Gson().toJson(new ErrorResponse("Error: already taken"));
			}

			//	[500] { "message": "Error: description" }

			response.status(200);
			response.type("application/json");
			return new Gson().toJson(authData);
		});
	}
}
