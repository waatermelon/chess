package server;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {

    private final GameService gameService;
    Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

    public Object createGame(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

    public Object joinGame(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

    public Object clearApplication(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

}
