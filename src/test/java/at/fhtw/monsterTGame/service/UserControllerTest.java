package at.fhtw.monsterTGame.service;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monsterTGame.controller.UserController;
import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private Request request;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService); // Übergabe von userService
        request = mock(Request.class);
    }

    @Test
    void testRegisterUser_Success() throws JsonProcessingException, SQLException {
        User newUser = new User("testuser", "password123");
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(newUser));
        when(userService.registerUser(any(User.class))).thenReturn(true);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }

    @Test
    void testRegisterUser_Fail_UserExists() throws JsonProcessingException, SQLException {
        User newUser = new User("existingUser", "password123");
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(newUser));
        when(userService.registerUser(any(User.class))).thenReturn(false);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.CONFLICT.code, response.getStatus());
    }

    @Test
    void testGetUserByToken_Success() throws SQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        User user = new User(1, "testuser", "hashedPassword", "valid-token");
        when(userService.findUserByToken("valid-token")).thenReturn(user);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testGetUserByToken_Fail_MissingToken() {
        when(request.getHeader("Authorization")).thenReturn(null);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testGetUser_Success() throws SQLException {
        when(request.getPathname()).thenReturn("/users/1");
        User user = new User(1, "testuser", "hashedPassword", "valid-token");
        when(userService.findUserById(1)).thenReturn(user);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testDeleteUser_Success() throws SQLException {
        when(request.getPathname()).thenReturn("/users/1");
        doNothing().when(userService).deleteUser(1);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testUpdateELO_Success() throws JsonProcessingException {
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(Map.of(
                "userId", 1,
                "eloChange", 10
        )));

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testUpdateCoins_Success() throws JsonProcessingException {
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(Map.of(
                "userId", 1,
                "coins", 50
        )));

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testUpdateWinLossRecord_Success() throws JsonProcessingException {
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(Map.of(
                "userId", 1,
                "won", true,
                "draw", false
        )));

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testFetchLeaderboard_Success() throws SQLException {
        when(request.getPathname()).thenReturn("/leaderboard");

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testGetUserByName_Success() throws SQLException {
        when(request.getPathname()).thenReturn("/users/testuser");
        User user = new User(1, "testuser", "hashedPassword", "valid-token");
        when(userService.findUserByName("testuser")).thenReturn(user);

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }

    @Test
    void testUpdateUserToken_Success() throws JsonProcessingException {
        when(request.getBody()).thenReturn(new ObjectMapper().writeValueAsString(Map.of(
                "userId", 1,
                "token", "new-valid-token"
        )));

        Response response = userController.handleRequest(request);
        assertNotNull(response, "Response darf nicht null sein!");
        assertEquals(HttpStatus.OK.code, response.getStatus());
    }
}
