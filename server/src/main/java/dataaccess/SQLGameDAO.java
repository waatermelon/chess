package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    Gson serializer = new Gson();

    public SQLGameDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            var statement = """            
                CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(64),
                blackUsername VARCHAR(64),
                gameName VARCHAR(64) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
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
    public void createGame(GameData gameData) throws DataAccessException{
        String statement = "INSERT INTO game " +
                "(gameID, whiteUsername, blackUsername, gameName, chessGame)" +
                "VALUES(?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, gameData.gameID());
            preparedStatement.setString(2, gameData.whiteUsername());
            preparedStatement.setString(3, gameData.blackUsername());
            preparedStatement.setString(4, gameData.gameName());
            preparedStatement.setString(5, serializer.toJson(gameData.game()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Read
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT " +
                "whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, gameID);
            var result = preparedStatement.executeQuery();
            result.next();

            var whiteUsername = result.getString("whiteUsername");
            var blackUsername = result.getString("blackUsername");
            var gameName = result.getString("gameName");
            var chessGame = serializer.fromJson(
                    result.getString("chessGame"), ChessGame.class);
            return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Update
    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String statement = "UPDATE game SET " +
                "whiteUsername = ?, blackUsername = ?, gameName = ?, " +
                "chessGame = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, serializer.toJson(gameData.game()));
            preparedStatement.setInt(5, gameData.gameID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // Delete
    @Override
    public void clear() {
        String statement = "TRUNCATE game";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> games = new ArrayList<>();
        var statement = "SELECT " +
                "gameID, whiteUsername, blackUsername, gameName, game FROM game";

        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            var result = preparedStatement.executeQuery();
            while (result.next()) {
                var gameID = result.getInt("gameID");
                var whiteUsername = result.getString("whiteUsername");
                var blackUsername = result.getString("blackUsername");
                var gameName = result.getString("gameName");
                var chessGame = serializer.fromJson(
                        result.getString("chessGame"), ChessGame.class);

                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
            }
        } catch (SQLException | DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }

        return games;
    }

    @Override
    public int getNextGameID() {
        String statement = "SELECT MAX(gameID) AS maxGameID FROM game";
        try (var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement(statement)) {
            var result = preparedStatement.executeQuery();
            if (result.next()) {
                int maxGameID = result.getInt("maxGameID");
                return maxGameID + 1;
            } else {
                return 1;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
