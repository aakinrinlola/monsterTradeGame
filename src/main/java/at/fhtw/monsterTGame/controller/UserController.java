package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryImpl;
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
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            // Pass unitOfWork to the repository or service layer
            UserRepositoryImpl userRepository = new UserRepositoryImpl(unitOfWork);

            if (request.getMethod() == Method.POST) {
                return processPostRequest(request, userRepository);
            } else if (request.getMethod() == Method.GET) {
                return processGetRequest(request, userRepository);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Unsupported HTTP method\"}");
        } catch (Exception ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private Response processPostRequest(Request request, UserRepositoryImpl userRepository) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User inputUser = mapper.readValue(request.getBody(), User.class);

            // Benutzername validieren
            if (inputUser.getUsername() == null || inputUser.getUsername().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Name is required\"}");
            }

            // Passwort-Hash validieren
            if (inputUser.getPassword() == null || inputUser.getPassword().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Password is required\"}");
            }

            // Benutzer existiert, Anmeldung durchführen
            try {
                User existingUser = userRepository.findByName(inputUser.getUsername());
                if (existingUser != null) {
                    String sessionToken = userService.authenticateUser(inputUser.getUsername(), inputUser.getPassword());
                    existingUser.setSessionToken(sessionToken);

                    // Erfolgslog
                    System.out.println("User logged in: " + existingUser.getUsername() + " (Token: " + existingUser.getSessionToken() + ")");

                    // JSON-Antwort erstellen
                    String jsonResponse = mapper.writeValueAsString(existingUser);
                    return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                }
            } catch (IllegalArgumentException ex) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid credentials\"}");
            }

            // Neuen Benutzer registrieren
            userRepository.saveUser(inputUser);

            // Erfolgslog
            System.out.println("User successfully registered: " + inputUser.getUsername());

            // Erfolgsantwort
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User successfully registered\"}");

        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + jsonEx.getMessage() + "\"}");
        }
    }

    private Response processGetRequest(Request request, UserRepositoryImpl userRepository) {
        try {
            Collection<User> allUsers = userService.getAllUsers();
            String jsonResponse = new ObjectMapper().writeValueAsString(allUsers);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (SQLException sqlEx) {
            // Loggen des Fehlers
            System.err.println("SQL Error: " + sqlEx.getMessage());
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Database error: " + sqlEx.getMessage() + "\"}");
        } catch (IllegalAccessException ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Access denied: " + ex.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Error serializing user data\"}");
        }
    }

}
