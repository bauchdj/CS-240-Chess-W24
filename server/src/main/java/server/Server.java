package server;

import spark.*;

import dataAccess.Database;
import dataAccess.UserDAO;
import dataAccess.GameDAO;
import dataAccess.AuthDAO;

import handlers.*;
import service.UserService;
import service.GameService;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Database db = new Database();

        UserDAO userDAO = new UserDAO(db);
        GameDAO gameDAO = new GameDAO(db);
        AuthDAO authDAO = new AuthDAO(db);

        UserService userService = new UserService(userDAO, authDAO);

        ClearHandler.clear(userDAO, gameDAO, authDAO);

        RegisterHandler.register(userService);
        LoginHandler.login(userService);
        LogoutHandler.logout(userService);

        GameService gameService = new GameService(gameDAO, authDAO);

        CreateHandler.createGame(gameService);
        JoinHandler.joinGame(gameService);
        ListGamesHandler.listGames(gameService);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
