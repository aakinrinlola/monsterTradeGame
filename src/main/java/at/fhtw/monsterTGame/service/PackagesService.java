package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.persistence.repository.PackagesRepository;
import at.fhtw.monsterTGame.persistence.repository.PackagesRepositoryImpl;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.*;

public class PackagesService {
    private final PackagesRepository packagesRepository;
    private final CardService cardService;
    private final UserRepository userRepository;

    public PackagesService() {
        this.packagesRepository = new PackagesRepositoryImpl(new UnitOfWork());
        this.cardService = new CardService();
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
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
        int coins = packagesRepository.getCoinsByToken(authToken);
        if (coins < 5) {
            throw new IllegalArgumentException("Not enough coins to buy a package.");
        }

        int packageId = packagesRepository.getFreePackageID();
        if (packageId == -1) {
            throw new IllegalStateException("No packages available for purchase.");
        }

        List<Cards> cards = packagesRepository.getCardsPackageId(packageId);
        int userId = userRepository.findToken(authToken).getUserId();

        for (Cards card : cards) {
            cardService.assignCardToUser(card.getCardId(), userId); // Karten einem Spieler zuweisen
        }

        packagesRepository.updateCoinsUser(authToken, coins - 5);
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
