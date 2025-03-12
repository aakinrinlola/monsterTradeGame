package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.service.CardService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import at.fhtw.monsterTGame.service.UserService;

import java.util.Collection;

public class CardController implements RestController {
    private final CardService cardService;
    private final UserService userService;

    public CardController() {
        this.cardService = new CardService();
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.GET) {
                return handleGet(request);
            } else if (request.getMethod() == Method.POST) {
                return handlePost(request);
            } else if (request.getMethod() == Method.DELETE) {
                return handleDelete(request);
            }

            return new Response(HttpStatus.METHOD_NOT_ALLOWED, ContentType.JSON, "{\"error\": \"Unsupported method\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleGet(Request request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing Authorization header\"}");
            }

            int userId = extractUserIdFromToken(token);
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

    private Response handlePost(Request request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing Authorization header\"}");
            }

            int userId = extractUserIdFromToken(token);
            Cards card = new ObjectMapper().readValue(request.getBody(), Cards.class);

            if (cardService.createCard(card)) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Card created successfully\"}");
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Failed to create card\"}");
            }
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleDelete(Request request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing Authorization header\"}");
            }

            int userId = extractUserIdFromToken(token);
            if (request.getPathParts().size() > 1) {
                String cardId = request.getPathParts().get(1);

                if (cardService.deleteCard(cardId, userId)) {
                    return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Card deleted successfully\"}");
                } else {
                    return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\": \"Card not found or does not belong to user\"}");
                }
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Card ID required for deletion\"}");
            }
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private int extractUserIdFromToken(String token) {
        if (token == null || !token.startsWith("authToken-")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        try {
            User user = userService.getAllUsers()
                    .stream()
                    .filter(u -> token.equals(u.getToken()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Token not associated with any user"));

            return user.getUserId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token validation failed: " + e.getMessage());
        }
    }
}
