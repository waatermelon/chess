package service;

import dataaccess.*;
import model.AuthData;
import model.LoginResult;
import model.UserData;
import spark.Request;
import spark.Response;

import java.util.UUID;

public class AuthService {

    AuthDAO authDAO;
    UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResult login(UserData userData) throws DataAccessException, BadRequestException, UnauthorizedException {
        try {
            userDAO.getUser(userData.username(), userData.password());
        } catch(Exception e) {
            throw new UnauthorizedException("Error accessing data from service: " + e.getMessage());
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), token);
        authDAO.createAuth(authData);

        return new LoginResult(authData.username(), authData.authToken());
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch(Exception e) {
            throw new UnauthorizedException("Error accessing data from service: " + e.getMessage());
        }

        authDAO.deleteAuth(auth);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
