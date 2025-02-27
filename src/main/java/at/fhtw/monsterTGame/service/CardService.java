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

    //Erstellt eine neue Karte und ordnet sie einem Benutzer zu
    public boolean createCard(Cards card, int userId) throws SQLException {
        if (card.getName() == null || card.getName().isEmpty()) {
            throw new IllegalArgumentException("Card name cannot be null or empty");
        }
        card.setUserId(userId); // Karte dem Benutzer zuweisen
        return cardRepo.save(card);
    }

    //Ruft alle Karten für einen bestimmten Benutzer ab
    public Collection<Cards> getCardsByUserId(int userId) throws SQLException {
        return cardRepo.findCardsByUserId(userId);
    }

    //Löscht eine Karte, falls sie dem Benutzer gehört
    public boolean deleteCard(int cardId, int userId) throws SQLException {
        Collection<Cards> userCards = getCardsByUserId(userId);

        boolean cardExists = userCards.stream()
                .anyMatch(card -> card.getCardId().equals(String.valueOf(cardId)));

        if (!cardExists) {
            throw new IllegalArgumentException("User does not own this card.");
        }

        return cardRepo.delete(String.valueOf(cardId));
    }

}
