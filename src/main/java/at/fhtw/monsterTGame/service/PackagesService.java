package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.persistence.repository.*;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.*;

public class PackagesService {
    private final PackagesRepository packagesRepository;
    private final CardService cardService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CardRepository cardRepository;

    public PackagesService() {
        this.packagesRepository = new PackagesRepositoryImpl(new UnitOfWork());
        this.cardService = new CardService();
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.userService = new UserService();
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
    }

    // Erstellt ein neues Paket mit Karten (ohne Besitzer)
    public boolean addNewPackage(String packageName, List<Cards> cards) throws SQLException, JsonProcessingException {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }

        for (Cards card : cards) {
            cardService.createCard(card); // Karten ohne Besitzer erstellen
        }

        return packagesRepository.addNewPackage(packageName, cards);
    }

    // Kauft ein Paket und weist Karten einem Benutzer zu
    public List<Cards> buyPackage(String authToken) throws SQLException {
        // Abrufen der aktuellen Coins des Benutzers
        int coins = packagesRepository.getCoinsByToken(authToken);

        // Überprüfen, ob der Benutzer genug Coins hat, um ein Paket zu kaufen
        if (coins < 5) {
            throw new IllegalArgumentException("Not enough coins to buy a package.");
        }

        // Abrufen der ID eines verfügbaren Pakets
        int packageId = packagesRepository.getFreePackageID();
        if (packageId == -1) {
            throw new IllegalStateException("No packages available for purchase.");
        }

        // Abrufen der Karten des gewählten Pakets
        List<Cards> cards = packagesRepository.getCardsPackageId(packageId);

        // Ermitteln der Benutzer-ID anhand des Authentifizierungstokens
        int userId = userRepository.findUserIdByToken(authToken);

        // Karten dem Benutzer zuweisen und speichern
        for (Cards card : cards) {
            card.setUserId(userId);
            cardRepository.save(card);
        }

        // Aktualisieren der Benutzer-Coins nach dem Kauf
        userService.updateCoins(userId, -5);

        // Markieren des Pakets als verkauft und Zuweisung an den Benutzer
        packagesRepository.setPackageAsSold(packageId, userId);

        return cards;
    }


    // Ruft alle Pakete eines Benutzers ab
    public List<Map<String, Object>> getPackagesByToken(String authToken) throws SQLException {
        var packages = packagesRepository.findPackagesFromToken(authToken);

        List<Map<String, Object>> result = new ArrayList<>();
        for (var pkg : packages) {
            Map<String, Object> packageMap = new HashMap<>();
            packageMap.put("id", pkg.getId());
            packageMap.put("name", pkg.getName());
            packageMap.put("cards", pkg.getCards());
            result.add(packageMap);
        }
        return result;
    }
}
