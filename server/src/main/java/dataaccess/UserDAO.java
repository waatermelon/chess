package dataaccess;

import model.UserData;

public interface UserDAO {

    // Create
    void createUser(UserData userData) throws DataAccessException, BadRequestException;

    // Read
    void getUser(String username, String password) throws DataAccessException;

    // Update
    // None for UserDAO

    // Delete
    void clear();
}
