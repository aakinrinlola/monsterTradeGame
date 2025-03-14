package at.fhtw.monsterTGame.controller;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.service.PackagesService;
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

public class PackageController implements RestController {
    private final PackagesService packagesService;

    public PackageController() {
        this.packagesService = new PackagesService();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            switch (request.getMethod()) {
                case POST:
                    if (request.getPathname().equals("/transactions/packages")) {
                        return handleBuyPackage(request);  // Paket kaufen
                    }
                    return handleCreatePackage(request);  // Neues Paket erstellen
                case GET:
                    return handleRetrievePackages(request);  // Pakete abrufen
                default:
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request method\"}");
            }
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    private Response handleCreatePackage(Request request) throws JsonProcessingException, SQLException {
        Map<String, Object> requestBody = new ObjectMapper().readValue(request.getBody(), Map.class);

        String packageName = (String) requestBody.get("name");
        List<Cards> cards = new ObjectMapper().convertValue(requestBody.get("cards"), new TypeReference<>() {});

        if (packageName == null || packageName.isEmpty()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Package name cannot be null or empty\"}");
        }

        if (cards.size() != 5) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"A package must contain exactly 5 cards.\"}");
        }

        if (packagesService.addNewPackage(packageName, cards)) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, String.format("{\"message\": \"Package '%s' created successfully.\"}", packageName));
        } else {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Failed to create package.\"}");
        }
    }

    private Response handleRetrievePackages(Request request) throws SQLException, JsonProcessingException {
        String token = extractToken(request);
        if (token == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing or invalid token\"}");
        }

        var packages = packagesService.getPackagesByToken(token);
        if (packages.isEmpty()) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\": \"No packages found\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, new ObjectMapper().writeValueAsString(packages));
    }

    private String extractToken(Request request) {
        String token = request.getHeader("Authorization");
        System.out.println("PackageControllerHeader: " + token);
        return (token != null && token.startsWith("Bearer ")) ? token.replace("Bearer ", "").trim() : null;
    }

    private Response handleBuyPackage(Request request) throws SQLException {
        String token = extractToken(request);
        if (token == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Missing or invalid token\"}");
        }

        try {
            List<Cards> purchasedCards = packagesService.buyPackage(token);

            String jsonResponse = new ObjectMapper().writeValueAsString(Map.of(
                    "message", "Package successfully purchased",
                    "cards", purchasedCards
            ));

            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
