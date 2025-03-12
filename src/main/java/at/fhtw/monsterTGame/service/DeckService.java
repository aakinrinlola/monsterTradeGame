package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.Deck;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.*;

import java.sql.SQLException;
import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public DeckService() {
        this.deckRepository = new DeckRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
    }

    public boolean createDeck(int userId, Deck deck) throws SQLException {
        if (deck == null || deck.isEmpty()) {
            throw new IllegalArgumentException("A deck must contain cards.");
        }
        return deckRepository.createDeck(userId, deck);
    }

    public Deck getDeckByUserId(int userId) throws SQLException {
        Deck deck = deckRepository.getDeckByUserId(userId);
        if (deck == null) {
            throw new IllegalArgumentException("No deck found for user with ID: " + userId);
        }
        return deck;
    }

    public boolean updateDeck(int userId, Deck deck) throws SQLException {
        return deckRepository.updateDeck(userId, deck);
    }

    public int findUserIdByToken(String token) throws SQLException {
        return userRepository.findUserIdByToken(token);
    }

    public List<Cards> getCardsByIds(List<String> cardIds, int userId) throws SQLException {
        List<Cards> cards = cardRepository.findCardsByIds(cardIds, userId);

        if (cards.size() != cardIds.size()) {
            throw new IllegalArgumentException("Some cards are invalid or do not belong to this user.");
        }
        return cards;
    }
}
