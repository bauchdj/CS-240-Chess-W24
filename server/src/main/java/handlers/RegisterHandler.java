package handlers;

import spark.Spark;
import com.google.gson.Gson;

import service.UserService;

import model.UserData;
import model.AuthData;

public class RegisterHandler {
	public static void register(UserService userService) {
		Spark.post("/user", (request, response) -> {
			UserData user = new Gson().fromJson(request.body(), UserData.class);

			if (user == null
					|| user.getUsername() == null
					|| user.getUsername().trim().isEmpty()
					|| user.getPassword() == null
					|| user.getPassword().trim().isEmpty()) {
				CreateResponse.halt400();
			} else {
				AuthData authData = userService.register(user);
				if (authData == null) CreateResponse.halt403();
				else CreateResponse.response200(response, authData);
			}

			return response.body();
		});
	}
}
