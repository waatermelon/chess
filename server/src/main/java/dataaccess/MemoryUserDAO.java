package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{

    ArrayList<UserData> db;

    public MemoryUserDAO(ArrayList<UserData> initUserData) {
        db = initUserData;
    }

    public MemoryUserDAO() {
        db = new ArrayList<UserData>();
    }

    // Create
    // Create TODO implement using DB
    @Override
    public void createUser(UserData userData) throws DataAccessException, BadRequestException {
        if (userData.password() == null || userData.username() == null) {
            throw new BadRequestException("");
        }

        for (UserData dbUserData: db) {
            if (dbUserData.equals(userData)) {
                throw new DataAccessException("");
            }
        }
        db.add(userData);
    }

    // Read
    @Override
    public void getUser(String username, String password) throws DataAccessException {
        for (UserData dbUserData: db) {
            if (dbUserData.username().equals(username) && dbUserData.password().equals(password)) {
                return;
            }
        }
        throw new DataAccessException("Password was incorrect, or user was not found.");
    }

    // Delete
    @Override
    public void clear() {
        db.clear();
    }

}
