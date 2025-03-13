package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;



public class SQLAuthDAOTests {

    static AuthDAO authDAO;
    static UserData userData;
    static UserData failUserData;
    static AuthData userAuthData;

    @BeforeAll
    public static void init() {
        authDAO = new SQLAuthDAO();
    }

    @BeforeEach
    public void setup() {
        authDAO.clear();

        userAuthData = new AuthData("The ultimate user", "1234my token1234");
        userData = new UserData("Fresh Cut yeh", "password", "sigmaboy@hotmail.com");
        failUserData = new UserData("Failure of a user", null, "f@gmail.com");
    }

    public AuthData findAuth() throws DataAccessException {
        String dbUser;
        String dbToken;

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, authToken FROM auth WHERE username = ?";
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, userAuthData.username());

            var result = preparedStatement.executeQuery();
            result.next();
            dbUser = result.getString("username");
            dbToken = result.getString("authToken");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new AuthData(dbUser, dbToken);
    }

    @Test
    @DisplayName("Positive Create Auth Test")
    public void successfulCreateAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);

        AuthData data = findAuth();
        assertEquals(userAuthData, new AuthData(data.username(), data.authToken()));
    }

    @Test
    @DisplayName("Negative Create Auth Test")
    public void failCreateAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);

        AuthData data = findAuth();
        assertNotEquals(null, new AuthData(data.username(), data.authToken()));
    }

    @Test
    @DisplayName("Positive Get Auth Test")
    public void successfulGetAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);
        AuthData data = authDAO.getAuth(userAuthData.authToken());

        assertEquals(userAuthData.username(), data.username());
    }

    @Test
    @DisplayName("Negative Get Auth Test")
    public void failGetAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(""));
    }

    @Test
    @DisplayName("Positive Delete Auth Test")
    public void successfulDeleteAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);
        AuthData data = authDAO.getAuth(userAuthData.authToken());
        authDAO.deleteAuth(data);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(userAuthData.authToken()));
    }

    @Test
    @DisplayName("Negative Delete Auth Test")
    public void failDeleteAuth() throws DataAccessException {
        authDAO.createAuth(userAuthData);
        AuthData data = authDAO.getAuth(userAuthData.authToken());
        authDAO.deleteAuth(new AuthData("fake", "fake token"));
        AuthData foundData = findAuth();
        assertEquals(data, foundData);
    }

    @Test
    @DisplayName("Positive Clear Test")
    public void successfulClear() throws DataAccessException {
        authDAO.createAuth(userAuthData);
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(userAuthData.authToken()));
    }

    @Test
    @DisplayName("Negative Clear Test")
    public void failClear() throws DataAccessException {
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(""));
    }
}
