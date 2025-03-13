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
import java.util.HashMap;
import java.util.Map;

public class PlayerStatsController implements RestController {
    private final UserService userService;

    public PlayerStatsController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            // Nur GET-Anfragen für "/stats" zulassen
            if (request.getMethod() == Method.GET && request.getPathname().equals("/stats")) {
                return getUserStats(request);
            }

            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request method or endpoint\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    private Response getUserStats(Request request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Missing or invalid Authorization header\"}");
            }

            User user = userService.findUserByToken(token);
            if (user == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"error\": \"User not found\"}");
            }

            // Benutzerstatistiken als Map speichern
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("username", user.getUsername());
            userStats.put("elo", user.getElo());
            userStats.put("wins", user.getGamesWon());
            userStats.put("losses", user.getGamesLost());
            userStats.put("draws", user.getGamesDrawn());
            userStats.put("games_played", user.getGamesPlayed());
            userStats.put("coins", user.getCoins());

            String jsonResponse = new ObjectMapper().writeValueAsString(userStats);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Error retrieving user stats: " + e.getMessage() + "\"}");
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
