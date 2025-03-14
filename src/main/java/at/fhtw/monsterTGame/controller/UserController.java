package at.fhtw.monsterTGame.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class UserController implements RestController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            String path = request.getPathname();
            Method method = request.getMethod();

            if (method == Method.POST && path.equals("/users")) {
                return processUserRegistration(request);
            }
            if (method == Method.GET && path.equals("/users/me")) {
                return processGetUserByToken(request);
            }
            if (method == Method.GET && path.equals("/users/me")) {
                return processGetUserByToken(request);
            }
            if (method == Method.PATCH && path.equals("/users/elo")) {
                return processUpdateELO(request);
            }
            if (method == Method.PATCH && path.equals("/users/coins")) {
                return processUpdateCoins(request);
            }
            if (method == Method.PATCH && path.equals("/users/stats")) {
                return processUpdateWinLossRecord(request);
            }
            if (method == Method.GET) {
                return processGetUser(request);
            }
            if (method == Method.DELETE) {
                return processDeleteUser(request);
            }

            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response processUserRegistration(Request request) {
        try {
            User user = new ObjectMapper().readValue(request.getBody(), User.class);
            boolean success = userService.registerUser(user);

            if (!success) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON,
                        "{\"error\": \"User already exists\"}");
            }
            return new Response(HttpStatus.CREATED, ContentType.JSON,
                    "{\"message\": \"User registered successfully\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid user data\"}");
        }
    }

    private Response processGetUserByToken(Request request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Missing or invalid Authorization header\"}");
            }

            User user = userService.findUserByToken(token);
            if (user == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"error\": \"User not found for this token\"}");
            }

            String jsonResponse = new ObjectMapper().writeValueAsString(user);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Error retrieving user\"}");
        }
    }

    private Response processGetUser(Request request) {
        String[] pathParts = request.getPathname().split("/");
        try {
            if (pathParts.length == 2) {
                Collection<User> users = userService.getAllUsers();
                return new Response(HttpStatus.OK, ContentType.JSON,
                        new ObjectMapper().writeValueAsString(users));
            }

            if (pathParts.length == 3) {
                String identifier = pathParts[2];

                User user;
                if (identifier.matches("\\d+")) {
                    user = userService.findUserById(Integer.parseInt(identifier));
                } else {
                    user = userService.findUserByName(identifier);
                }

                if (user != null) {
                    return new Response(HttpStatus.OK, ContentType.JSON,
                            new ObjectMapper().writeValueAsString(user));
                }
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"error\": \"User not found\"}");
            }
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Error retrieving user\"}");
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                "{\"error\": \"Invalid request format\"}");
    }

    private Response processDeleteUser(Request request) {
        try {
            String[] pathParts = request.getPathname().split("/");
            if (pathParts.length == 3) {
                int userId = Integer.parseInt(pathParts[2]);
                userService.deleteUser(userId);
                return new Response(HttpStatus.OK, ContentType.JSON,
                        "{\"message\": \"User deleted successfully\"}");
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request format\"}");

        } catch (NumberFormatException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid user ID\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Database error\"}");
        }
    }
    private Response processUpdateELO(Request request) {
        try {
            Map<String, Object> requestBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            int userId = (int) requestBody.get("userId");
            int eloChange = (int) requestBody.get("eloChange");

            userService.updateELOAndGamesPlayed(userId, eloChange);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"ELO and games played updated\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request data\"}");
        }
    }

    private Response processUpdateCoins(Request request) {
        try {
            Map<String, Object> requestBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            int userId = (int) requestBody.get("userId");
            int coins = (int) requestBody.get("coins");

            userService.updateCoins(userId, coins);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Coins updated\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request data\"}");
        }
    }

    private Response processUpdateWinLossRecord(Request request) {
        try {
            Map<String, Object> requestBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            int userId = (int) requestBody.get("userId");
            boolean won = (boolean) requestBody.get("won");
            boolean draw = (boolean) requestBody.get("draw");

            userService.updateWinLossRecord(userId, won, draw);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Win/Loss record updated\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request data\"}");
        }
    }

    private String extractToken(Request request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.replace("Bearer ", "").trim();
    }
}
