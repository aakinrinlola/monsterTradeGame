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

public class CardRepositoryImpl implements CardRepository {
    private final UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Collection<Cards> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE user_id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            Collection<Cards> cards = new ArrayList<>();
            while (resultSet.next()) {
                Cards card = new Cards(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        ElementTypeEnum.valueOf(resultSet.getString("element_type")),
                        CardTypeEnum.valueOf(resultSet.getString("category")),
                        resultSet.getFloat("damage"),
                        resultSet.getInt("user_id")
                );
                cards.add(card);
            }
            return cards;
        }
    }

    @Override
    public boolean saveCard(Cards card) throws SQLException {
        String sql = "INSERT INTO cards (title, element_type, category, damage, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, card.getTitle());
            statement.setString(2, card.getElementType().toString());
            statement.setString(3, card.getCategory().toString());
            statement.setFloat(4, card.getDamage());
            statement.setInt(5, card.getUserId());
            int rowsAffected = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteCard(int cardId) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, cardId);
            int rowsAffected = statement.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsAffected > 0;
        }
    }
}
