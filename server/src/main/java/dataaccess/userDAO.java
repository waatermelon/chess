package dataaccess;

import model.UserData;

public interface userDAO {

    // Create
    void createUser(UserData userData);

    // Read
    UserData getUser(String username);

    // Update
    // None for UserDAO

    // Delete
    void clear();
}
