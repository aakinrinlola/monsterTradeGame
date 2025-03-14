package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.persistence.repository.*;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.monsterTGame.model.User;

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
        User user = userRepository.findToken(authToken);
        if (user == null) {
            throw new IllegalArgumentException("Invalid token or user not found.");
        }

        int coins = user.getCoins();
        if (coins < 5) {
            throw new IllegalArgumentException("Not enough coins to buy a package.");
        }

        int packageId = packagesRepository.getFreePackageID();
        if (packageId == -1) {
            throw new IllegalStateException("No packages available for purchase.");
        }

        List<Cards> cards = packagesRepository.getCardsPackageId(packageId);
        for (Cards card : cards) {
            card.setUserId(user.getUserId());
            cardRepository.save(card);
        }

        userService.updateCoins(user.getUserId(), -5);
        packagesRepository.setPackageAsSold(packageId, user.getUserId());

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
