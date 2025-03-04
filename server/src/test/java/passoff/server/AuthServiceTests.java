package passoff.server;

import dataaccess.*;
import model.AuthData;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;
import service.AuthService;
import service.GameService;
import service.UserService;

;

public class AuthServiceTests {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;
    static AuthService authService;
    static UserService userService;
    static GameService gameService;
    static UserData userData;
    static UserData failUserData;

    @BeforeAll
    static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        authService = new AuthService(authDAO, userDAO);
        userService = new UserService(authDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @BeforeEach
    void setup() {
        authService.clear();
        userService.clear();
        gameService.clear();
        userData = new UserData("Sigma boy", "password", "sigmaboy@hotmail.com");
        failUserData = new UserData("Failure of a human", null, "f@gmail.com");
    }

    @Test
    @DisplayName("Positive Login Test")
    void successLogin() throws BadRequestException, DataAccessException, UnauthorizedException {
        userService.register(userData);
        LoginResult loginResult = authService.login(userData);
        Assertions.assertEquals(authDAO.getAuth(loginResult.authToken()).authToken(), loginResult.authToken());
        Assertions.assertEquals(authDAO.getAuth(loginResult.authToken()).username(), loginResult.username());
    }

    @Test
    @DisplayName("Negative Login Test")
    void failLogin() {
        Assertions.assertThrows(BadRequestException.class, () -> authService.login(failUserData));
    }

    @Test
    @DisplayName("Positive Logout Test")
    void successLogout() throws BadRequestException, DataAccessException, UnauthorizedException {
        userService.register(userData);
        LoginResult loginResult = authService.login(userData);
        authService.logout(loginResult.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("Negative Logout Test")
    void failLogout() throws BadRequestException, DataAccessException, UnauthorizedException {
        userService.register(userData);
        authService.login(userData);
        Assertions.assertThrows(BadRequestException.class, () -> authService.logout(null));
    }
}
