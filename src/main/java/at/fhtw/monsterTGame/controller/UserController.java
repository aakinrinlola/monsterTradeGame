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
            UserRepositoryImpl userRepository = new UserRepositoryImpl(unitOfWork);

            if (request.getMethod() == Method.POST) {
                return processPostRequest(request, userRepository);
            } else if (request.getMethod() == Method.GET) {
                return processGetRequest(request, userRepository);
            } else if (request.getMethod() == Method.DELETE) {
                return processDeleteRequest(request, userRepository);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Unsupported HTTP method\"}");
        } catch (Exception ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }
    // POST-Handler - hier wird entweder ein neuer User registriert oder ein vorhandener User authentifiziert
    private Response processPostRequest(Request request, UserRepositoryImpl userRepository) {
        try {

            //Body v Request wird zu einem UserObjekt umgewandel
            ObjectMapper mapper = new ObjectMapper();
            User inputUser = mapper.readValue(request.getBody(), User.class);

            if (inputUser.getUsername() == null || inputUser.getUsername().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Name is required\"}");
            }

            if (inputUser.getPassword() == null || inputUser.getPassword().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Password is required\"}");
            }

            try {
                // Schauen, ob der User schon existiert
                User existingUser = userRepository.findByName(inputUser.getUsername());
                if (existingUser != null) {
                    //wenn er existiert SessionToken erstellt
                    String sessionToken = userService.authenticateUser(inputUser.getUsername(), inputUser.getPassword());
                    existingUser.setSessionToken(sessionToken);
                    String jsonResponse = mapper.writeValueAsString(existingUser);
                    return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                }
            } catch (IllegalArgumentException ex) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid credentials\"}");
            }

            // Wenn der User nicht existiert, wird er registriert
            userRepository.saveUser(inputUser);
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User successfully registered\"}");

        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + jsonEx.getMessage() + "\"}");
        }
    }

    // GET-Handler - liefert eine Liste aller User zurück
    private Response processGetRequest(Request request, UserRepositoryImpl userRepository) {
        try {
            Collection<User> allUsers = userService.getAllUsers();
            String jsonResponse = new ObjectMapper().writeValueAsString(allUsers);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (SQLException sqlEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Database error: " + sqlEx.getMessage() + "\"}");
        } catch (IllegalAccessException ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Access denied: " + ex.getMessage() + "\"}");
        } catch (JsonProcessingException jsonEx) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Error serializing user data\"}");
        }
    }

    private Response processDeleteRequest(Request request, UserRepositoryImpl userRepository) {
        try {
            String pathname = request.getPathname();
            String[] pathParts = pathname.split("/");
            int userId = Integer.parseInt(pathParts[pathParts.length - 1]); // Assuming URL is /users/{id}
            userService.deleteUser(userId);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"User deleted successfully\"}");
        } catch (SQLException sqlEx) {
            // Fehler bei der Datenbank
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + sqlEx.getMessage() + "\"}");
        } catch (NumberFormatException ex) {
            // Fehlerhafte User-ID
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid user ID\"}");
        }
    }
}