package at.fhtw.monsterTGame.controller;

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
    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST && request.getPathname().equals("/payments/transactions")) {
                return processPayment(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request method or path\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response processPayment(Request request) throws SQLException {
        // Auth-Token aus dem Header extrahieren
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing or invalid token\"}");
        }
        token = token.replace("Bearer ", "").trim();

        try {
            // Zahlung verarbeiten
            var transactionResult = paymentService.processPayment(token);

            // JSON-Antwort erstellen
            String jsonResponse = new ObjectMapper().writeValueAsString(Map.of(
                    "message", "Payment successful",
                    "transactionDetails", transactionResult
            ));

            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (IllegalArgumentException | IllegalStateException | JsonProcessingException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
