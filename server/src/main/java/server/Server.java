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
            AuthData authData = userService.register(user);
            response.status(200);
            return authData.getAuthToken();
            //return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, authToken));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
