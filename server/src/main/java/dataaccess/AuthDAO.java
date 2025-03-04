
package dataaccess;

import model.AuthData;

public interface AuthDAO {

    // Create
    void createAuth(AuthData authData) throws DataAccessException;

    // Read
    AuthData getAuth(String authToken) throws DataAccessException;

    // Update/Delete
    void deleteAuth(AuthData authData);

    // Delete
    void clear();

}
