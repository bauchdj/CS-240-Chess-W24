package server;

import spark.*;
import com.google.gson.Gson;

import dataAccess.Database;
import dataAccess.UserDAO;
import dataAccess.GameDAO;
import dataAccess.AuthDAO;

import service.UserService;

import model.UserData;
import model.GameData;
import model.AuthData;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Database db = new Database();

        UserDAO userDAO = new UserDAO(db);
        GameDAO gameDAO = new GameDAO(db);
        AuthDAO authDAO = new AuthDAO(db);

        UserService userService = new UserService(userDAO, authDAO);

        // Register your endpoints and handle exceptions here.
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

        Spark.delete("/session", (request, response) -> {
            String authToken = request.headers("authorization");

            if (authToken == null || authToken.isEmpty()) {
                response.status(401); // Unauthorized
                response.type("application/json");
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }

            boolean success = userService.logout(new AuthData(authToken));
            if (!success) {
                response.status(401); // Unauthorized
                response.type("application/json");
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }

            response.status(200);
            response.type("application/json");
            return "{}";
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
