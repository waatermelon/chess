package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            var statement = """            
                    CREATE TABLE IF NOT EXISTS AUTHENTICATION (
                                    username VARCHAR(255),
                                    authToken VARCHAR(255),
                                    PRIMARY KEY (authToken)
                                    )""";
            var conn = DatabaseManager.getConnection();
            try (var createTableStatement = conn.prepareStatement(statement)) {
                createTableStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    // Read
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

    }

    // Update/Delete
    @Override
    public void deleteAuth(AuthData authData) {

    }

    // Delete
    @Override
    public void clear() {

    }
}
