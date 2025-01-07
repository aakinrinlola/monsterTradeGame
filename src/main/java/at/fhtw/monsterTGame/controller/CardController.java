package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.service.CardService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;

public class CardController implements RestController {
    private final CardService cardService;

    public CardController() {
        this.cardService = new CardService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return handleGet(request);
        }
        return new Response(HttpStatus.METHOD_NOT_ALLOWED, ContentType.JSON, "{\"error\": \"Unsupported method\"}");
    }

    private Response handleGet(Request request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing Authorization header\"}");
            }

            int userId = extractUserIdFromToken(token); // Token validieren und userId extrahieren
            Collection<Cards> cards = cardService.getCardsByUserId(userId);

            if (cards.isEmpty()) {
                return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "");
            }

            String jsonResponse = new ObjectMapper().writeValueAsString(cards);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (IllegalArgumentException ex) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (Exception ex) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private int extractUserIdFromToken(String token) {
        // Beispiel-Token-Logik: "Bearer-1" -> userId = 1
        try {
            String[] parts = token.split("-");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token format");
        }
    }
}
