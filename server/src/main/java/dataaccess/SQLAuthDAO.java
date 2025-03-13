package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            var statement = """
                CREATE TABLE IF NOT EXISTS auth (
                username VARCHAR(64),
                authToken VARCHAR(64),
                
                )""";
            var conn = DatabaseManager.getConnection();
            try (var createTableStatement = conn.prepareStatement(statement)) {
                createTableStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e.getMessage());
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
