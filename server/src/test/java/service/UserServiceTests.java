package service;

import dataaccess.*;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;

;

public class UserServiceTests {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static UserService userService;
    static UserData userData;
    static UserData failUserData;

    @BeforeAll
    static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(authDAO, userDAO);
    }

    @BeforeEach
    void setup() {
        userService.clear();
        userData = new UserData("Sigma boy", "password", "sigmaboy@hotmail.com");
        failUserData = new UserData("Failure of a human", null, "f@gmail.com");
    }


    @Test
    @DisplayName("Positive Register Test")
    void successRegister() throws BadRequestException, DataAccessException {
        RegisterResult registerResult = userService.register(userData);
        Assertions.assertEquals(authDAO.getAuth(registerResult.authToken()).authToken(), registerResult.authToken());
        Assertions.assertEquals(authDAO.getAuth(registerResult.authToken()).username(), registerResult.username());
    }

    @Test
    @DisplayName("Negative Register Test")
    void failRegister() {
        Assertions.assertThrows(BadRequestException.class, () -> userService.register(failUserData));
    }
}
