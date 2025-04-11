package service;

import dataaccess.*;
import model.AuthData;
import model.LoginResult;
import model.UserData;

import java.util.UUID;

public class AuthService {

    AuthDAO authDAO;
    UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResult login(UserData userData) throws DataAccessException, BadRequestException, UnauthorizedException {
        if (userData.password() == null || userData.username() == null) {
            throw new BadRequestException("");
        }

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

    public void logout(String authToken) throws UnauthorizedException, BadRequestException {
        if (authToken == null) {
            throw new BadRequestException("");
        }
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch(Exception e) {
            throw new UnauthorizedException("Error accessing data from service: " + e.getMessage());
        }

        authDAO.deleteAuth(auth);
    }

    public boolean validAuth(String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
