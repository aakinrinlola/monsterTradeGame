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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardController implements RestController {
    private final UserService userService;

    public LeaderboardController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.GET && request.getPathname().equals("/scoreboard")) {
                return fetchLeaderboard();
            }

            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request method\"}");

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response fetchLeaderboard() throws SQLException {
        List<User> users = userService.getScoreboard();

        List<Map<String, Object>> leaderboardData = users.stream()
                .map(user -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", user.getUsername());
                    userData.put("elo", user.getElo());
                    userData.put("wins", user.getGamesWon());
                    userData.put("losses", user.getGamesLost());
                    userData.put("draws", user.getGamesDrawn());
                    return userData;
                })
                .collect(Collectors.toList());

        try {
            String jsonResponse = new ObjectMapper().writeValueAsString(leaderboardData);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Failed to process leaderboard data\"}");
        }
    }

}
