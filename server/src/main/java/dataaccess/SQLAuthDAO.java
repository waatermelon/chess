package dataaccess;

import model.AuthData;

import java.sql.SQLException;

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
                PRIMARY KEY (authToken)
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
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auth (username, authToken) VALUES(?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authData.username());
            preparedStatement.setString(2, authData.authToken());

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Read
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username, authToken FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authToken);
            var result = preparedStatement.executeQuery();
            result.next();

            var username = result.getString("username");
            return new AuthData(username, authToken);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Update/Delete
    @Override
    public void deleteAuth(AuthData authData) {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection() ) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authData.authToken());

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Delete
    @Override
    public void clear() {
        String statement = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
