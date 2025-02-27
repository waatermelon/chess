package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface GameDAO {

    // Create
    void createGame(GameData gameData);

    // Read
    GameData getGame(int gameID) throws DataAccessException;

    // Update
    void updateGame(GameData gameData);

    // Delete
    void clear();

    ArrayList<GameData> listGames();

}
