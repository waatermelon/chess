package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    // Create
    void createGame(GameData gameData);

    // Read
    void getGame(int gameID);

    // Update
    void updateGame(GameData gameData);

    // Delete
    void clear();

    ArrayList<GameData> listGames();

}
