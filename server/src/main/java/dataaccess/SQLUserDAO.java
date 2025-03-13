package dataaccess;

import model.UserData;

import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

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
            preparedStatement.setString(2,
                    BCrypt.hashpw(userData.password(), BCrypt.gensalt(6)));
            preparedStatement.setString(3, userData.email());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Read
    @Override
    public void getUser(String username, String password) throws DataAccessException {
        String statement = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);

            var result = preparedStatement.executeQuery();
            if (!result.next()) {
                throw new DataAccessException("No user with matching credentials");
            }

            String storedHash = result.getString("password");
            if (!BCrypt.checkpw(password, storedHash)) {
                throw new DataAccessException("No user with matching credentials");
            }

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
