package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import model.JoinRequestData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class GameHandler {

    private final GameService gameService;
    Gson serializer = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        ArrayList<GameData> gamesList = gameService.listGames(authToken);
        response.status(200);
        return "{games: " + gamesList + "}";
    }

    public Object createGame(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        int gameID = gameService.createGame(authToken);
        response.status(200);
        return "{gameID: " + gameID + "}";
    }

    public Object joinGame(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        JoinRequestData jrq = serializer.fromJson(request.body(), JoinRequestData.class);
        gameService.joinGame(authToken, jrq.playerColor(), jrq.gameID());
        response.status(200);
        return "{}";
    }

    public void clear() throws Exception {
        gameService.clear();
    }

}
