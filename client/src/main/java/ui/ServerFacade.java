package ui;


import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import websocket.commands.CommandExtension;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class ServerFacade {

    Gson serializer = new Gson();

    String url;
    String authToken;
    String port;

    WebSocketClient WSClient;

    public ServerFacade(String port) {
        this.port = port;
        url = "http://localhost:" + port;
    }

    public void WebSocketConnection() {
        try {
            WSClient = new WebSocketClient(port);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMessage(UserGameCommand.CommandType commandType, int gameID) {
        CommandExtension command = new CommandExtension(
                commandType,
                authToken,
                gameID,
                null,
                null,
                null
        );
        WSClient.sendMessage(serializer.toJson(command));
    }

    public void sendMessage(UserGameCommand.CommandType commandType, int gameID,
                            String username, ChessGame.TeamColor color, ChessMove move) {
        CommandExtension command = new CommandExtension(
                commandType,
                authToken,
                gameID,
                username,
                color,
                move
        );
        System.out.println(serializer.toJson(command));
        WSClient.sendMessage(serializer.toJson(command));
    }

    public boolean register(String username, String password, String email) {
        String jsonBody = convertArgsToJson(
                new String[] {"username", "password", "email"},
                new String[] {username, password, email});

        Map response = createRequest("POST", "/user", jsonBody);
        if (response.containsKey("Exception")) {
            return false;
        } else {
            authToken = (String) response.get("authToken");
            return true;
        }
    }

    public boolean login(String username, String password) {
        String jsonBody = convertArgsToJson(
                new String[] {"username", "password"},
                new String[] {username, password});

        Map response = createRequest("POST", "/session", jsonBody);

        if (response.containsKey("Exception")) {
            return false;
        } else {
            authToken = (String) response.get("authToken");
            return true;
        }
    }

    public boolean logout() {
        Map response = createRequest("DELETE", "/session", null);
        if (response.containsKey("Exception")) {
            return false;
        } else {
            authToken = null;
            return true;
        }
    }

    public ArrayList<LinkedTreeMap> listGames() {
        Map response = createRequest("GET", "/game", null);
        if (response.containsKey("Exception")) {
            return null;
        } else {
            String jsonResponse = new Gson().toJson(response);
            return (ArrayList<LinkedTreeMap>) response.get("games");
        }
    }

    public boolean joinGame(String playerColor, double gameID) {
        String jsonBody = convertArgsToJson(
                new String[] {"playerColor", "gameID"},
                new String[] {playerColor, Integer.toString((int) gameID)});
        Map response = createRequest("PUT", "/game", jsonBody);

        return !response.containsKey("Exception");
    }

    public boolean viewGame(double gameID) {
        String jsonBody = convertArgsToJson(
                new String[] {"playerColor", "gameID"},
                new String[] {"SPECTATOR", Integer.toString((int) gameID)});

        Map response = createRequest("PUT", "/game", jsonBody);

        return !response.containsKey("Exception");
    }

    public double createGame(String name) {
        String jsonBody = convertArgsToJson(
                new String[] {"gameName"},
                new String[] {name});
        Map response = createRequest("POST", "/game", jsonBody);
        if (response.containsKey("Exception")) {
            return 0;
        } else {
            return (double) response.get("gameID");
        }
    }

    private Map createRequest(String method, String path, String body) {
        Map returnValues;
        try {
            URI uri = new URI(url + path);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);
            addAuth(http);
            addBody(http, body);
            http.connect();
            if (wasException(http)) {
                return Map.of("Exception", 400);
            }
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                returnValues = new Gson().fromJson(inputStreamReader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            return Map.of("Exception", 400);
        }
        return returnValues;
    }

    private String convertArgsToJson(String[]keys, String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i = 0; i < keys.length; ++i) {
            sb.append(keys[i]).append(": ").append(values[i]);
            if (i < keys.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private void addAuth(HttpURLConnection http) {
        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
    }

    private void addBody(HttpURLConnection http, String body) throws IOException {
        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private boolean wasException(HttpURLConnection http) {
        try {
            if (http.getResponseCode() == 401) {
                return true;
            }
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public String getUserLoggedIn() {
        return (authToken != null) ? "LOGGED_IN" : "LOGGED_OUT";
    }
}