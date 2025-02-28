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
    @Override
    public void createUser(UserData userData) {
        for (UserData dbUserData: db) {
            if (dbUserData.equals(userData)) {
                return;
            }
        }
        db.add(userData);
    }

    // Read
    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        for (UserData dbUserData: db) {
            if (dbUserData.username().equals(username) && dbUserData.password().equals(password)) {
                return dbUserData;
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
