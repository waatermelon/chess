package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.ArrayList;


public class SQLGameDAOTests {

    static SQLGameDAO gameDAO;
    static GameData gameData1;
    static GameData gameData2;
    static ChessGame chessGame;

    @BeforeAll
    public static void init() {
        gameDAO = new SQLGameDAO();
        chessGame = new ChessGame();
    }

    @BeforeEach
    public void setup() {
        gameDAO.clear();
        gameData1 = new GameData(1, "whiteguy",
                "blackguy", "game1", chessGame);
        gameData2 = new GameData(2, "some dude",
                "the other dude", "game2", chessGame);
    }

    private GameData findGame(int gameID) throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, " +
                "gameName, game FROM game WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, gameID);

            var result = preparedStatement.executeQuery();
            result.next();
            return new GameData(
                result.getInt("gameID"),
                result.getString("whiteUsername"),
                result.getString("blackUsername"),
                result.getString("gameName"),
                gameDAO.serializer.fromJson(result.getString("game"), ChessGame.class)
            );
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("Positive Create Game Test")
    public void successfulCreateGame() throws DataAccessException {
        gameDAO.createGame(gameData1);
        GameData foundGame = findGame(1);
        assertEquals(gameData1.gameName(), foundGame.gameName());
    }

    @Test
    @DisplayName("Negative Create Game Test")
    public void failCreateGame() throws DataAccessException {
        gameDAO.createGame(gameData1);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData1));
    }

    @Test
    @DisplayName("Positive Get Game Test")
    public void successfulGetGame() throws DataAccessException {
        gameDAO.createGame(gameData1);
        GameData retrieved = gameDAO.getGame(1);
        assertEquals(gameData1.gameName(), retrieved.gameName());
    }

    @Test
    @DisplayName("Negative Get Game Test")
    public void failGetGame() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(999));
    }

    @Test
    @DisplayName("Positive Update Game Test")
    public void successfulUpdateGame() throws DataAccessException {
        gameDAO.createGame(gameData1);
        GameData updated = new GameData(1, "newWhite", "newBlack", "Updated Game", chessGame);
        gameDAO.updateGame(updated);
        GameData retrieved = gameDAO.getGame(1);
        assertEquals("newWhite", retrieved.whiteUsername());
        assertEquals("newBlack", retrieved.blackUsername());
    }

    @Test
    @DisplayName("Negative Update Game Test")
    public void failUpdateGame() {
        GameData errorGame = new GameData(999, "nobody", "nobody", "Ghost Game", chessGame);
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(errorGame));
    }

    @Test
    @DisplayName("Positive List Games Test")
    public void successfulListGames() throws DataAccessException {
        gameDAO.createGame(gameData1);
        gameDAO.createGame(gameData2);
        ArrayList<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("Negative List Games Test")
    public void failListGames() {
        ArrayList<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    @DisplayName("Positive Clear Test")
    public void successfulClear() throws DataAccessException {
        gameDAO.createGame(gameData1);
        gameDAO.clear();
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(1));
    }

    @Test
    @DisplayName("Positive Next GameID Test")
    public void successfulGetNextGameID() {
        assertEquals(1, gameDAO.getNextGameID());
    }

    @Test
    @DisplayName("Positive Next GameID Test")
    public void failGetNextGameID() throws DataAccessException {
        gameDAO.createGame(gameData1);
        gameDAO.createGame(gameData2);

        assertEquals(3, gameDAO.getNextGameID());
    }
}