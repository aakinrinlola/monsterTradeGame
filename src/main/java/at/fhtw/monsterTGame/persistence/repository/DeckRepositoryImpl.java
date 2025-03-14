package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.Deck;
import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import at.fhtw.monsterTGame.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckRepositoryImpl implements DeckRepository {
    private final UnitOfWork unitOfWork;

    public DeckRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean createDeck(int userId, Deck deck) throws SQLException {
        String sql = "INSERT INTO decks (user_id, cards) VALUES (?, ?)";

        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setObject(2, deck.getCards()); // Karten als JSON speichern

            int affectedRows = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return affectedRows > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public Deck getDeckByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM decks WHERE user_id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                List<Cards> cards = fetchDeckCards(userId);
                return new Deck(resultSet.getInt("id"), userId, cards);
            }
        }
        // Statt `null`, gib ein leeres Deck zurück
        return new Deck(0, userId, new ArrayList<>());
    }


    @Override
    public boolean updateDeck(int userId, Deck deck) throws SQLException {
        String sql = "UPDATE decks SET cards = ? WHERE user_id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setObject(1, deck.getCards());
            statement.setInt(2, userId);

            int affectedRows = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return affectedRows > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw e;
        }
    }

    private List<Cards> fetchDeckCards(int userId) throws SQLException {
        String sql = """
            SELECT c.id, c.name, c.damage, c.element, c.type
            FROM cards c
            JOIN decks d ON c.id = ANY(d.cards)
            WHERE d.user_id = ?
        """;

        List<Cards> cards = new ArrayList<>();
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cards.add(new Cards(
                        resultSet.getString("id"), // ID als String
                        resultSet.getString("name"), // Name als String
                        ElementTypeEnum.valueOf(resultSet.getString("element")), // ENUM für Element
                        CardTypeEnum.valueOf(resultSet.getString("type")), // ENUM für Kartentyp
                        resultSet.getDouble("damage"), // DAMAGE bleibt double
                        resultSet.getObject("user_id", Integer.class) // Integer kann null sein
                ));
            }

        }
        return cards;
    }
}
