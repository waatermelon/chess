package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            var statement = """            
                    CREATE TABLE IF NOT EXISTS USER (
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

    // Create
    @Override
    public void createUser(UserData userData) throws DataAccessException, BadRequestException {

    }

    // Read
    @Override
    public void getUser(String username, String password) throws DataAccessException {

    }

    // Delete
    @Override
    public void clear() {

    }

}
