package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> db;

    public MemoryGameDAO(ArrayList<GameData> initGameData) {
        db = initGameData;
    }

    public MemoryGameDAO() {
        db = new ArrayList<GameData>();
    }

    // Create
    // Create TODO implement using DB
    @Override
    public void createGame(GameData gameData) throws DataAccessException{
        for (GameData dbGameData: db) {
            if (dbGameData.equals(gameData)) {
                throw new DataAccessException("");
            }
        }
        db.add(gameData);
    }

    // Read
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData dbGameData: db) {
            if (dbGameData.gameID() == gameID) {
                return dbGameData;
            }
        }
        throw new DataAccessException("Game was not found.");
    }

    // Update
    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        for (int i = 0; i < db.size(); ++i) {
            if (db.get(i).gameID() == gameData.gameID()) {
                db.set(i, gameData);
                return;
            }
        }
        this.createGame(gameData);
    }

    // Delete
    @Override
    public void clear() {
        db.clear();
    }

    @Override
    public ArrayList<GameData> listGames() {
        return db;
    }

    @Override
    public int getNextGameID() {
        return db.size() + 1;
    }

}
