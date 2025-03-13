package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
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

    public Object register(Request request, Response response) throws BadRequestException, AlreadyTakenException {
        UserData userData = serializer.fromJson(request.body(), UserData.class);
        RegisterResult registerResult;
        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("");
        }

        try {
            registerResult = userService.register(userData);
        } catch (Exception e) {
            throw new AlreadyTakenException("");
        }
        System.out.println("registering");
        response.status(200);
        return new Gson().toJson(registerResult);
    }

    public void clear() throws Exception {
        userService.clear();
    }
}
