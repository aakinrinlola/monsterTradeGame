package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.CardRepository;
import at.fhtw.monsterTGame.persistence.repository.CardRepositoryImpl;

import java.sql.SQLException;
import java.util.Collection;

public class CardService {
    private final CardRepository cardRepo;

    public CardService() {
        this.cardRepo = new CardRepositoryImpl(new UnitOfWork());
    }

    // Erstellt eine Karte ohne Besitzer (z. B. für ein Paket)
    public boolean createCard(Cards card) throws SQLException {
        if (card.getName() == null || card.getName().isEmpty()) {
            throw new IllegalArgumentException("Card name cannot be null or empty");
        }
        return cardRepo.save(card);
    }

    // Ordnet eine existierende Karte einem Benutzer zu
    public boolean assignCardToUser(String cardId, int userId) throws SQLException {
        return cardRepo.assignCardToUser(String.valueOf(cardId), userId);
    }

    // Ruft alle Karten eines bestimmten Benutzers ab
    public Collection<Cards> getCardsByUserId(int userId) throws SQLException {
        return cardRepo.findCardsByUserId(userId);
    }

    // Löscht eine Karte, falls sie dem Benutzer gehört
    public boolean deleteCard(String cardId, int userId) throws SQLException {
        Collection<Cards> userCards = getCardsByUserId(userId);

        boolean cardExists = userCards.stream()
                .anyMatch(card -> card.getCardId().equals(cardId)); // String-Vergleich mit equals()

        if (!cardExists) {
            throw new IllegalArgumentException("User does not own this card.");
        }

        return cardRepo.delete(cardId); // String statt int
    }

}
