package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.CardRepositoryImpl;

import java.sql.SQLException;
import java.util.Collection;

public class CardService {
    private final CardRepositoryImpl cardRepo;

    public CardService() {
        this.cardRepo = new CardRepositoryImpl(new UnitOfWork());
    }

    public Collection<Cards> getCardsByUserId(int userId) throws SQLException {
        return cardRepo.findByUserId(userId);
    }

    public boolean addCard(Cards card) throws SQLException {
        return cardRepo.saveCard(card);
    }

    public boolean removeCard(int cardId) throws SQLException {
        return cardRepo.deleteCard(cardId);
    }
}
