package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.Battle;
import at.fhtw.monsterTGame.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class BattleController implements RestController {
    private final UserService userService;

    public BattleController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST) {
                return processBattleRequest(request);
            }

            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request method\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response processBattleRequest(Request request) {
        try {
            // Extrahiere das Token aus dem Header
            String playerOneToken = request.getHeader("Authorization");
            if (playerOneToken == null || !playerOneToken.startsWith("Bearer ")) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing or invalid Authorization header\"}");
            }
            playerOneToken = playerOneToken.replace("Bearer ", "");

            // JSON-Request-Body parsen
            Map<String, Object> requestBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            if (!requestBody.containsKey("opponentId")) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Opponent ID is required\"}");
            }

            // Spieler-IDs aus Token und Request extrahieren
            int playerOneId = userService.findUserByToken(playerOneToken).getUserId();
            int playerTwoId = (int) requestBody.get("opponentId");

            if (playerOneId <= 0 || playerTwoId <= 0) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid player IDs\"}");
            }

            // Battle starten
            Battle battle = new Battle(playerOneId, playerTwoId);
            String battleOutcome = battle.commenceBattle();

            // JSON-Antwort erstellen
            String jsonResponse = new ObjectMapper().writeValueAsString(Map.of(
                    "message", "Battle successfully completed",
                    "result", battleOutcome,
                    "log", battle.getBattleHistory()
            ));
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
