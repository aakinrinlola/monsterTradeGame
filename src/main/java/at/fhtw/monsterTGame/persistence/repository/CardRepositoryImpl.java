package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import at.fhtw.monsterTGame.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private final UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Cards findById(String id) throws SQLException {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Cards(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        ElementTypeEnum.valueOf(resultSet.getString("element")),
                        CardTypeEnum.valueOf(resultSet.getString("type")),
                        resultSet.getDouble("damage"),
                        resultSet.getObject("user_id", Integer.class) // Kann null sein
                );
            }
        }
        return null;
    }

    @Override
    public Collection<Cards> findAll() throws SQLException {
        String sql = "SELECT * FROM cards";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Collection<Cards> cards = new ArrayList<>();

            while (resultSet.next()) {
                cards.add(new Cards(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        ElementTypeEnum.valueOf(resultSet.getString("element")),
                        CardTypeEnum.valueOf(resultSet.getString("type")),
                        resultSet.getDouble("damage"),
                        resultSet.getObject("user_id", Integer.class)
                ));
            }
            return cards;
        }
    }

    @Override
    public boolean save(Cards card) throws SQLException {
        String sql = "INSERT INTO cards (id, name, damage, element, type, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, card.getCardId());
            statement.setString(2, card.getName());
            statement.setDouble(3, card.getDamage());
            statement.setString(4, card.getElementType().toString());
            statement.setString(5, card.getCategory().toString());
            if (card.getUserId() != null) {
                statement.setInt(6, card.getUserId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            int rowsAffected = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsAffected > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public boolean delete(String cardId) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, cardId);
            int rowsAffected = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean assignCardToUser(String cardId, Integer userId) throws SQLException {
        String sql = "UPDATE cards SET user_id = ? WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            if (userId != null) {
                statement.setInt(1, userId);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }
            statement.setString(2, cardId);
            boolean result = statement.executeUpdate() > 0;
            unitOfWork.commitTransaction();
            return result;
        }
    }

    @Override
    public Collection<Cards> findCardsByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE user_id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            Collection<Cards> cards = new ArrayList<>();

            while (resultSet.next()) {
                cards.add(new Cards(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        ElementTypeEnum.valueOf(resultSet.getString("element")),
                        CardTypeEnum.valueOf(resultSet.getString("type")),
                        resultSet.getDouble("damage"),
                        resultSet.getObject("user_id", Integer.class)
                ));
            }
            return cards;
        }
    }

    @Override
    public List<Cards> findCardsByIds(List<String> cardIds, Integer userId) throws SQLException {
        if (cardIds.isEmpty()) {
            throw new IllegalArgumentException("The card ID list is empty.");
        }

        String placeholders = String.join(",", cardIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = "SELECT * FROM cards WHERE id IN (" + placeholders + ") AND user_id = ?";

        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            for (int i = 0; i < cardIds.size(); i++) {
                statement.setString(i + 1, cardIds.get(i));
            }
            statement.setInt(cardIds.size() + 1, userId);

            ResultSet resultSet = statement.executeQuery();
            List<Cards> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(new Cards(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        ElementTypeEnum.valueOf(resultSet.getString("element")),
                        CardTypeEnum.valueOf(resultSet.getString("type")),
                        resultSet.getDouble("damage"),
                        resultSet.getObject("user_id", Integer.class)
                ));
            }
            return cards;
        }
    }

    @Override
    public boolean updateCardUser(String cardId, Integer newUserId) throws SQLException {
        String sql = "UPDATE cards SET user_id = ? WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            if (newUserId != null) {
                statement.setInt(1, newUserId);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }
            statement.setString(2, cardId);
            boolean result = statement.executeUpdate() > 0;
            unitOfWork.commitTransaction();
            return result;
        }
    }
}
