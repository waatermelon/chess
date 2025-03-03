package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginResult;
import model.UserData;
import service.AuthService;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;

public class AuthHandler {

    private final AuthService authService;
    Gson serializer = new Gson();

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    public Object login(Request request, Response response) throws DataAccessException {
        UserData userData = serializer.fromJson(request.body(), UserData.class);
        LoginResult loginResult = authService.login(userData);
        response.status(200);
        return serializer.toJson(loginResult);
    }

    public Object logout(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        authService.logout(authToken);
        response.status(200);
        return "{}";
    }

    public void clear() {
        authService.clear();
    }

}
