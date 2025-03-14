package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface CardRepository {
    //ID als String
    Cards findById(String id) throws SQLException;
    Collection<Cards> findAll() throws SQLException;
    boolean save(Cards card) throws SQLException;
    // hier auch ID
    boolean delete(String cardId) throws SQLException;

    boolean assignCardToUser(String cardId, Integer userId) throws SQLException; // Token → userId geändert
    Collection<Cards> findCardsByUserId(Integer userId) throws SQLException; // Token → userId geändert
    List<Cards> findCardsByIds(List<String> cardIds, Integer userId) throws SQLException; // id als String

    boolean updateCardUser(String cardId, Integer newUserId) throws SQLException;

}
