package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ArrayList<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new UnauthorizedException("Error accessing data: " + e.getMessage());
        }

        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName)
            throws DataAccessException, UnauthorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new UnauthorizedException("Error accessing data: " + e.getMessage());
        }

        int gameID = gameDAO.getNextGameID();
        gameDAO.createGame(new GameData(gameID, null, null, gameName, null));
        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new UnauthorizedException("Error accessing data: " + e.getMessage());
        }
        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new BadRequestException("Error accessing data: " + e.getMessage());
        }
        if (playerColor == null || (!playerColor.contains("WHITE") && !playerColor.contains("BLACK"))) {
            throw new BadRequestException("Not given a color");
        }


        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();
        if (playerColor.equals("WHITE")) {
            if (whiteUser != null) {
                throw new AlreadyTakenException("Player already exists");
            } else {
                whiteUser = authData.username();
            }
        } else if (playerColor.equals("BLACK")) {
            if (blackUser != null) {
                throw new AlreadyTakenException("Player already exists");
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
