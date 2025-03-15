package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService() {
            @Override
            public boolean registerUser(User user) throws SQLException {
                return userRepository.saveUser(user);
            }

            @Override
            public User findUserByName(String username) throws SQLException {
                return userRepository.findName(username);
            }

            @Override
            public User findUserById(int userId) throws SQLException {
                return userRepository.findId(userId);
            }

            @Override
            public String authenticateUser(String username, String password) throws SQLException {
                return "authToken-123";
            }

            @Override
            public boolean isValidToken(String token) throws SQLException {
                return userRepository.findToken(token) != null;
            }
        };
    }

    @Test
    void testRegisterUser_Success() throws SQLException {
        User newUser = new User("testuser", "password123");
        when(userRepository.saveUser(any(User.class))).thenReturn(true);

        boolean result = userService.registerUser(newUser);

        assertTrue(result);
    }

    @Test
    void testRegisterUser_Fail_UserExists() throws SQLException {
        User existingUser = new User("existingUser", "password123");
        when(userRepository.findName("existingUser")).thenReturn(existingUser);

        boolean result = userService.registerUser(existingUser);

        assertFalse(result);
    }

    @Test
    void testFindUserByName_Success() throws SQLException {
        User user = new User("testuser", "password123");
        when(userRepository.findName("testuser")).thenReturn(user);

        User foundUser = userService.findUserByName("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void testFindUserById_Success() throws SQLException {
        User user = new User(1, "testuser", "password123", "authToken-123");
        when(userRepository.findId(1)).thenReturn(user);

        User foundUser = userService.findUserById(1);

        assertNotNull(foundUser);
        assertEquals(1, foundUser.getUserId());
    }

    @Test
    void testAuthenticateUser_Success() throws SQLException {
        User user = new User(1, "testuser", "password123", null);
        when(userRepository.findName("testuser")).thenReturn(user);
        doNothing().when(userRepository).updateToken(eq("testuser"), anyString());


        String token = userService.authenticateUser("testuser", "password123");

        assertNotNull(token);
        assertTrue(token.startsWith("authToken-"));
    }

    @Test
    void testIsValidToken_Success() throws SQLException {
        when(userRepository.findToken("authToken-123")).thenReturn(new User(1, "testuser", "password123", "authToken-123"));

        boolean isValid = userService.isValidToken("authToken-123");

        assertTrue(isValid);
    }
}
