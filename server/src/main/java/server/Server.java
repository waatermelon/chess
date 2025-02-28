package server;

import dataaccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();

    AuthService authService = new AuthService(authDAO, userDAO);
    GameService gameService = new GameService(authDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO);

    AuthHandler authHandler = new AuthHandler(authService);
    GameHandler gameHandler = new GameHandler(gameService);
    UserHandler userHandler = new UserHandler(userService);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //TODO
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}