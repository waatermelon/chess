package server;

import dataaccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import org.eclipse.jetty.websocket.api.Session;
import spark.*;

import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static AuthDAO authDAO = new SQLAuthDAO();
    public static GameDAO gameDAO = new SQLGameDAO();
    public static UserDAO userDAO = new SQLUserDAO();

    AuthService authService = new AuthService(authDAO, userDAO);
    GameService gameService = new GameService(authDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO);

    AuthHandler authHandler = new AuthHandler(authService);
    GameHandler gameHandler = new GameHandler(gameService);
    UserHandler userHandler = new UserHandler(userService);

    public static ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", authHandler::login);
        Spark.delete("/session", authHandler::logout);
        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(AlreadyTakenException.class, this::alreadyTakenException);
        Spark.exception(BadRequestException.class, this::badRequestException);
        Spark.exception(UnauthorizedException.class, this::unauthorizedException);
        Spark.exception(Exception.class, this::exception);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        sessions = new ConcurrentHashMap<>();
        Spark.stop();
        Spark.awaitStop();
    }

    public void clear() {
        try {
            authHandler.clear();
            userHandler.clear();
            gameHandler.clear();
            sessions = new ConcurrentHashMap<>();
        } catch (Exception e) {
            System.out.println("error server on clear");
        }
    }

    private Object clear(Request request, Response response) throws Exception {
        authHandler.clear();
        gameHandler.clear();
        userHandler.clear();
        sessions = new ConcurrentHashMap<>();
        response.status(200);
        return "{}";
    }

    private void alreadyTakenException(AlreadyTakenException e, Request request, Response response) {
        response.status(403);
        response.body("{\"message\": \"Error: already taken\"}");
    }

    private void badRequestException(BadRequestException e, Request request, Response response) {
        response.status(400);
        response.body("{ \"message\": \"Error: bad request\" }");
    }

    private void unauthorizedException(UnauthorizedException e, Request request, Response response) {
        response.status(401);
        response.body("{ \"message\": \"Error: unauthorized\" }");
    }

    private void exception(Exception e, Request request, Response response) {
        response.status(500);
        response.body("{ \"message\": Error: TSETING" + e.getMessage() + " }");
    }
}