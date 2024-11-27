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
import java.util.Collection;

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

            // Benutzername validieren
            if (inputUser.getName() == null || inputUser.getName().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Name is required\"}");
            }

            // Passwort-Hash validieren
            if (inputUser.getPasswordHash() == null || inputUser.getPasswordHash().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Password is required\"}");
            }

            try {
                // Benutzer existiert, Anmeldung durchführen
                User existingUser = userManager.getUserByName(inputUser.getName());
                if (existingUser != null) {
                    String sessionToken = userManager.authenticateUser(inputUser.getName(), inputUser.getPasswordHash());
                    existingUser.setSessionToken(sessionToken);

                    // Erfolgslog
                    System.out.println("User logged in: " + existingUser.getName() + " (Token: " + existingUser.getSessionToken() + ")");

                    // JSON-Antwort erstellen
                    String jsonResponse = mapper.writeValueAsString(existingUser);
                    return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                }
            } catch (IllegalArgumentException ex) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid credentials\"}");
            }

            // Neuen Benutzer registrieren
            userManager.addUser(inputUser);

            // Erfolgslog
            System.out.println("User successfully registered: " + inputUser.getName());

            // Erfolgsantwort
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User successfully registered\"}");

        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + jsonEx.getMessage() + "\"}");
        }
    }

    private Response processGetRequest(Request request) {
        try {
            Collection<User> allUsers = userManager.getAllUsers();
            String jsonResponse = new ObjectMapper().writeValueAsString(allUsers);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

        } catch (IllegalAccessException ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Access denied: " + ex.getMessage() + "\"}");
        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Error serializing user data\"}");
        }
    }

}
