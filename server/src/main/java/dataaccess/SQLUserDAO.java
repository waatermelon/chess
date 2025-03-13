package dataaccess;

import model.UserData;

import java.sql.SQLException;

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
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Create
    @Override
    public void createUser(UserData userData) throws DataAccessException, BadRequestException {
        String statement = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, userData.password());
            preparedStatement.setString(3, userData.email());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Read
    @Override
    public void getUser(String username, String password) throws DataAccessException {
        String statement = "SELECT username, password, email FROM user WHERE " +
                "username = ? AND password = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            var result = preparedStatement.executeQuery();
            result.next();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Delete
    @Override
    public void clear() {
        String statement = "TRUNCATE user";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
