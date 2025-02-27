package dataaccess;

import model.UserData;

public interface UserDAO {

    // Create
    void createUser(UserData userData);

    // Read
    UserData getUser(String username) throws DataAccessException;

    // Update
    // None for UserDAO

    // Delete
    void clear();
}
