package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.RegisterResult;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    private final UserService userService;
    Gson serializer = new Gson();
    
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request request, Response response) throws DataAccessException {
        UserData userData = serializer.fromJson(request.body(), UserData.class);
        RegisterResult registerResult = userService.register(userData);
        response.status(200);
        return new Gson().toJson(registerResult);
    }

    public void clear() {
        userService.clear();
    }
}
