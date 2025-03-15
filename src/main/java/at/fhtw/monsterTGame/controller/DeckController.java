package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.Deck;
import at.fhtw.monsterTGame.service.DeckService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DeckController implements RestController {
    private final DeckService deckService;

    public DeckController() {
        this.deckService = new DeckService();
    }
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            return switch (request.getMethod()) {
                case GET -> processGetRequest(request);
                case PUT -> processUpdateDeck(request);
                default -> new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Unsupported request method\"}");
            };
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response processGetRequest(Request request) throws SQLException, JsonProcessingException {
        String token = extractToken(request);
        if (token == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Authorization header missing or invalid\"}");
        }

        int userId = deckService.findUserIdByToken(token);
        Deck deck = deckService.getDeckByUserId(userId);

        String jsonResponse = new ObjectMapper().writeValueAsString(Map.of(
                "userId", deck.getUserId(),
                "cards", deck.getCards()
        ));

        return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
    }

    private Response processUpdateDeck(Request request) throws SQLException, JsonProcessingException {
        String token = extractToken(request);
        if (token == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Authorization required\"}");
        }

        int userId = deckService.findUserIdByToken(token);

        List<String> cardIds;
        try {
            cardIds = new ObjectMapper().readValue(request.getBody(), new TypeReference<>() {});
            if (cardIds == null || cardIds.size() != 4) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"You must provide exactly 4 card IDs\"}");
            }
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request body format\"}");
        }

        List<Cards> cards = deckService.getCardsByIds(cardIds, userId);
        if (cards.size() != 4) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Some cards are invalid or do not belong to the user\"}");
        }

        try {
            Deck existingDeck = deckService.getDeckByUserId(userId);
            existingDeck.setCards(cards);
            deckService.updateDeck(userId, existingDeck);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Deck updated successfully\"}");
        } catch (IllegalArgumentException e) {
            Deck newDeck = new Deck(userId, cards);
            boolean created = deckService.createDeck(userId, newDeck);
            if (!created) {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Could not create deck\"}");
            }
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Deck created successfully\"}");
        }
    }

    private String extractToken(Request request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "").trim();
        }
        return null;
    }
}
