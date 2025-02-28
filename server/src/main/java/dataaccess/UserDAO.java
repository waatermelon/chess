package dataaccess;

import model.UserData;

public interface UserDAO {

    // Create
    void createUser(UserData userData);

    // Read
    void getUser(String username, String password) throws DataAccessException;

    // Update
    // None for UserDAO

    // Delete
    void clear();
}
