package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import java.sql.SQLException;
import java.util.Collection;

public interface CardRepository {
    Collection<Cards> findByUserId(int userId) throws SQLException;
    boolean saveCard(Cards card) throws SQLException;
    boolean deleteCard(int cardId) throws SQLException;
}
