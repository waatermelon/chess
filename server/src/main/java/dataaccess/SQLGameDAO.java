package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            var statement = """            
                    CREATE TABLE IF NOT EXISTS game (
                                    `gameID` INT NOT NULL AUTO_INCREMENT,
                                    `whiteUsername` VARCHAR(64),
                                    `blackUsername` VARCHAR(64),
                                    `gameName` VARCHAR(64) NOT NULL,
                                    `game` TEXT NOT NULL,
                                    PRIMARY KEY (gameID)
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
    public void createGame(GameData gameData) throws DataAccessException{

    }

    // Read
    @Override
    public GameData getGame(int gameID) throws DataAccessException {

    }

    // Update
    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    // Delete
    @Override
    public void clear() {
        db.clear();
    }

    @Override
    public ArrayList<GameData> listGames() {

    }

    @Override
    public int getNextGameID() {

    }

}
