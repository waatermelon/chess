package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void listGames() {
        //TODO
    }

    public void createGame() {
        //TODO
    }

    public void joinGame() {
        //TODO
    }

    public void clear() {
        this.authDAO.clear();
        this.gameDAO.clear();
    }

}
