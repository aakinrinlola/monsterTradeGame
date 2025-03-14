package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.service.PackagesService;
import at.fhtw.monsterTGame.service.PaymentService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Map;

public class PaymentController implements RestController {
    private final PackagesService packagesService;

    public PaymentController() {
        this.packagesService = new PackagesService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST && request.getPathname().equals("/transactions/packages")) {
                return handleBuyPackage(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request method or path\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleBuyPackage(Request request) throws SQLException {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                    "{\"error\": \"Missing or invalid Authorization header. Expected format: 'Bearer <token>'\"}");
        }

        token = token.replace("Bearer ", "").trim();

        try {
            var purchasedCards = packagesService.buyPackage(token);

            String jsonResponse = new ObjectMapper().writeValueAsString(Map.of(
                    "message", "Package successfully purchased",
                    "cards", purchasedCards
            ));

            return new Response(HttpStatus.CREATED, ContentType.JSON, jsonResponse);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
