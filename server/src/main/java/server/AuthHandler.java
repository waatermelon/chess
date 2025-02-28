package server;

import com.google.gson.Gson;
import service.AuthService;
import spark.Request;
import spark.Response;

public class AuthHandler {

    private final AuthService authService;
    Gson serializer = new Gson();

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    public Object login(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

    public Object logout(Request request, Response response) {
        //implement with service//TODO
        return null;
    }

    public void clear() {
        authService.clear();
    }

}
