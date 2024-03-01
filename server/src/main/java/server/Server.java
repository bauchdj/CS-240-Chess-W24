package server;

import dataAccess.*;
import spark.*;

import handlers.*;
import service.*;

public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        DataAccess db = new MySQLDatabase();

        UserDAO userDAO = new UserDAO(db);
        GameDAO gameDAO = new GameDAO(db);
        AuthDAO authDAO = new AuthDAO(db);

        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        ClearHandler.clear(clearService);

        UserService userService = new UserService(userDAO, authDAO);
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
