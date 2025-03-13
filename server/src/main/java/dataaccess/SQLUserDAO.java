package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            var statement = """            
                CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(64) NOT NULL,
                password VARCHAR(64) NOT NULL,
                email VARCHAR(64),
                PRIMARY KEY (username)
                )""";
            var conn = DatabaseManager.getConnection();
            try (var createTableStatement = conn.prepareStatement(statement)) {
                createTableStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e.getMessage());
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
