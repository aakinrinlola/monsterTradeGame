package at.fhtw.monsterTGame.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Collection;

// UserController verwaltet Benutzeranfragen und ruft die entsprechenden Services auf
public class UserController implements RestController {
    private final UserService userService;

    // Konstruktor, der eine Instanz von UserService initialisiert
    public UserController() {
        this.userService = new UserService();
    }

    // Zentrale Methode zur Verarbeitung von HTTP-Anfragen
    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST) {
                if (request.getPathname().equals("/users")) {
                    return processPostUserRegistration(request);
                } else if (request.getPathname().equals("/sessions")) {
                    return processPostUserLogin(request);
                }
            } else if (request.getMethod() == Method.GET) {
                return processGetUser(request);
            } else if (request.getMethod() == Method.DELETE) {
                return processDeleteUser(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST - Benutzerregistrierung
    private Response processPostUserRegistration(Request request) {
        try {
            User user = new ObjectMapper().readValue(request.getBody(), User.class);
            boolean success = userService.registerUser(user);
            if (success) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
            } else {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"error\": \"User already exists\"}");
            }
        } catch (Exception e) {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"error\": \"User already exists\"}");
        }
    }

    // POST - Benutzeranmeldung (Login)
    private Response processPostUserLogin(Request request) {
        try {
            User user = new ObjectMapper().readValue(request.getBody(), User.class);
            String token = userService.authenticateUser(user.getUsername(), user.getPassword());
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\": \"" + token + "\"}");
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid JSON format\"}");
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid username or password\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Database error\"}");
        }
    }

    // GET - Abrufen von Benutzerdaten (Alle, per Username oder ID)
    private Response processGetUser(Request request) {
        String[] pathParts = request.getPathname().split("/");
        try {
            if (pathParts.length == 2) { // `/users` → Alle Benutzer abrufen
                Collection<User> users = userService.getAllUsers();
                String jsonResponse = new ObjectMapper().writeValueAsString(users);
                return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

            } else if (pathParts.length == 3) {
                String identifier = pathParts[2];

                // Prüfen, ob identifier eine Zahl (ID) ist oder ein Username
                if (identifier.matches("\\d+")) { // Falls es eine Zahl ist → User per ID abrufen
                    int userId = Integer.parseInt(identifier);
                    User user = userService.findUserById(userId);
                    if (user != null) {
                        String jsonResponse = new ObjectMapper().writeValueAsString(user);
                        return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                    } else {
                        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\": \"User not found\"}");
                    }

                } else { // Falls es KEINE Zahl ist → User per Name abrufen
                    User user = userService.findUserByName(identifier);
                    if (user != null) {
                        String jsonResponse = new ObjectMapper().writeValueAsString(user);
                        return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
                    } else {
                        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\": \"User not found\"}");
                    }
                }
            }
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Error retrieving user\"}");
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request format\"}");
    }

    // DELETE - Löschen eines Benutzers
    private Response processDeleteUser(Request request) {
        try {
            String[] pathParts = request.getPathname().split("/");
            if (pathParts.length == 3) {
                int userId = Integer.parseInt(pathParts[2]);
                userService.deleteUser(userId);
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"User deleted successfully\"}");
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request format\"}");
            }
        } catch (NumberFormatException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid user ID\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Database error\"}");
        }
    }
}
