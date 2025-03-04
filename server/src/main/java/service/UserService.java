package service;

import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {

    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(UserData userData) throws BadRequestException, DataAccessException {
        try {
            userDAO.createUser(userData);
        } catch(Exception e) {
            throw new BadRequestException("Error accessing data from service: " + e.getMessage());
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), token);
        authDAO.createAuth(authData);
        return new RegisterResult(authData.username(), authData.authToken());
    }

    public void clear() {
        authDAO.clear();
        userDAO.clear();
    }

}
