package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class UserController implements RestController {
    private final UserService userManager;

    public UserController() {
        this.userManager = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST) {
                return processPostRequest(request);
            } else if (request.getMethod() == Method.GET) {
                return processGetRequest(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Unsupported HTTP method\"}");
        } catch (Exception ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private Response processPostRequest(Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User inputUser = mapper.readValue(request.getBody(), User.class);

            // User Validierung
            if (inputUser.getUsername() == null || inputUser.getUsername().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Username is required\"}");
            }

            // Validierung des Pws
            if (inputUser.getPassword() == null || inputUser.getPassword().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Password is required\"}");
            }

            try {
                // Benutzer existiert, Anmeldung durchführen
                User existingUser = userManager.findUserByUsername(inputUser.getUsername());
                if (existingUser != null) {
                    String sessionToken = userManager.loginUser(inputUser.getUsername(), inputUser.getPassword());
                    existingUser.setToken(sessionToken);

                    // Loggen
                    System.out.println("User logged in: " + existingUser.getUsername() + " (Token: " + existingUser.getToken() + ")");

                    // Antwort erstellen
                    String jsonResponse = mapper.writeValueAsString(existingUser);
                    return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                }
            } catch (IllegalArgumentException ex) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid credentials\"}");
            }

            // Neuen Benutzer registrieren
            userManager.registerUser(inputUser);

            // Erfolgslog
            System.out.println("User successfully registered: " + inputUser.getUsername());

            // Antwort bei Erfolg
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User successfully registered\"}");

        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + jsonEx.getMessage() + "\"}");
        }
    }

    private Response processGetRequest(Request request) {
        try {
            List<User> allUsers = (List<User>) userManager.findAllUsers();

            String jsonResponse = new ObjectMapper().writeValueAsString(allUsers);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Error serializing user data\"}");
        }
    }
}
