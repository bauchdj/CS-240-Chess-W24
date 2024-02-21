package handlers;

import spark.Spark;
import com.google.gson.Gson;

import service.UserService;

import model.UserData;
import model.AuthData;

public class LoginHandler {
	public static void login(UserService userService) {
		Spark.post("/session", (request, response) -> {
			UserData user = new Gson().fromJson(request.body(), UserData.class);
			AuthData authData = userService.login(user);
			if (authData == null) CreateResponse.halt401();
			else CreateResponse.response200(response, authData);

			return response.body();
		});
	}
}
