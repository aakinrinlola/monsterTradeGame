package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryImpl;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.model.User;

import java.sql.SQLException;
import java.util.Map;

public class PaymentService {
    private final UserRepository userRepository;

    public PaymentService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public Map<String, Object> processPayment(String token) throws SQLException {
        // Benutzer anhand des Tokens abrufen
        User user = userRepository.findToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        // Kosten der Transaktion (z. B. für ein Kartenpaket)
        int transactionCost = 5;

        if (user.getCoins() < transactionCost) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Coins abziehen und aktualisieren
        int newBalance = user.getCoins() - transactionCost;
        userRepository.updateCoinsForExtraPrice(user.getUserId(), -transactionCost);

        // Rückgabe der Transaktionsdetails
        return Map.of(
                "username", user.getUsername(),
                "previousBalance", user.getCoins(),
                "newBalance", newBalance,
                "transactionCost", transactionCost,
                "status", "Payment successful"
        );
    }
}
