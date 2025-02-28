package server;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    private final UserService userService;
    Gson gson = new Gson();
    
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request request, Response response) {
        //implement with service//TODO
        return null;
    }
}
