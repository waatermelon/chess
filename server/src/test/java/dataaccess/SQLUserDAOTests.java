package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;



public class SQLUserDAOTests {

    static SQLUserDAO userDAO;
    static UserData validUser;
    static UserData duplicateUser;

    @BeforeAll
    public static void init() {
        userDAO = new SQLUserDAO();
    }

    @BeforeEach
    public void setup() {
        userDAO.clear();
        validUser = new UserData("thesigma", "wututut", "teste@gmail.com");
        duplicateUser = new UserData("thesigma", "asdfasdf", "anotherone@wow.ok");
    }

    private UserData findUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);

            var resulted = preparedStatement.executeQuery();
            resulted.next();
            return new UserData(
            resulted.getString("username"),
            resulted.getString("password"),
            resulted.getString("email")
            );
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("Positive Create User Test")
    public void successfulCreateUser() throws DataAccessException, BadRequestException {
        userDAO.createUser(validUser);
        UserData user = findUser(validUser.username());


        assertEquals(validUser.username(), user.username());
        assertEquals(validUser.email(), user.email());
        assertTrue(BCrypt.checkpw(validUser.password(), user.password()));
    }

    @Test
    @DisplayName("Negative Create User Test")
    public void failCreateUser() throws DataAccessException, BadRequestException {
        userDAO.createUser(validUser);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
    }

    @Test
    @DisplayName("Positive Get User Test")
    public void successfulGetUser() throws DataAccessException, BadRequestException {
        userDAO.createUser(validUser);

        assertDoesNotThrow(() -> userDAO.getUser(validUser.username(), validUser.password()));
    }

    @Test
    @DisplayName("Negative Get User Test")
    public void failGetUser() throws DataAccessException, BadRequestException {
        userDAO.createUser(validUser);

        assertThrows(DataAccessException.class, () ->
                userDAO.getUser(validUser.username(), "sigma"));
    }

    @Test
    @DisplayName("Positive Clear Test")
    public void successfulClear() throws DataAccessException, BadRequestException {
        userDAO.createUser(validUser);
        userDAO.clear();

        assertThrows(DataAccessException.class, () ->
                userDAO.getUser(validUser.username(), validUser.password()));
    }

    @Test
    @DisplayName("Negative Clear Test")
    public void failClear() {
        userDAO.clear();

        assertThrows(DataAccessException.class, () ->
                userDAO.getUser("afsdasdf", "asdfdsfadfs"));
    }
}