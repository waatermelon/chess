package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            var statement = """            
                    CREATE TABLE IF NOT EXISTS GAME (
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
