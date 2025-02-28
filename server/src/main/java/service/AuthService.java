package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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

    public LoginResult login(UserData userData) throws DataAccessException {
        try {
            // confirms user exists, otherwise exception is thrown
            userDAO.getUser(userData.username(), userData.password());
        } catch(Exception e) {
            throw new DataAccessException("Error accessing data from service: " + e.getMessage());
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, userData.username());
        authDAO.createAuth(authData);

        return new LoginResult(authData.username(), authData.authToken());
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch(Exception e) {
            throw new DataAccessException("Error accessing data from service: " + e.getMessage());
        }

        authDAO.deleteAuth(auth);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
