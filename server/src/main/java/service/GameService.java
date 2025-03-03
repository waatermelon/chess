package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.UUID;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error accessing data: " + e.getMessage());
        }

        return gameDAO.listGames();
    }

    public int createGame(String authToken) throws DataAccessException{
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error accessing data: " + e.getMessage());
        }

        int gameID = gameDAO.getNextGameID();
        gameDAO.createGame(new GameData(gameID, null, null, null, null));
        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException{
        //TODO
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error accessing data: " + e.getMessage());
        }
        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error accessing data: " + e.getMessage());
        }
        if (!playerColor.contains("WHITE") && !playerColor.contains("BLACK"))
            throw new DataAccessException("Not given a color");

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            if (whiteUser != null) {
                throw new DataAccessException("Player already exists");
            } else {
                whiteUser = authData.username();
            }
        } else if (playerColor.equals("BLACK")) {
            if (blackUser != null) {
                throw new DataAccessException("Player already exists");
            } else {
                blackUser = authData.username();
            }
        }

        gameDAO.updateGame(new GameData(game.gameID(), whiteUser, blackUser, game.gameName(), game.game()));

    }

    public void clear() {
        this.authDAO.clear();
        this.gameDAO.clear();
    }

}
