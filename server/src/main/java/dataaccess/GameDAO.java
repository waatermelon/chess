package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface GameDAO {

    // Create
    void createGame(GameData gameData) throws DataAccessException;

    // Read
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames();
    int getNextGameID();

    // Update
    void updateGame(GameData gameData) throws DataAccessException;

    // Delete
    void clear();


}
