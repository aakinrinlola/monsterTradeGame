package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.Cards;
import at.fhtw.monsterTGame.model.Packages;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PackagesRepositoryImpl implements PackagesRepository {
    private final UnitOfWork unitOfWork;
    private final UserRepository userRepository;

    public PackagesRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
        this.userRepository = new UserRepositoryImpl(unitOfWork);
    }

    @Override
    public int getCoinsByToken(String authToken) throws SQLException {
        String sql = "SELECT coins FROM users WHERE token = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, authToken);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("coins");
            }
        }
        return 0;
    }

    @Override
    public int getFreePackageID() throws SQLException {
        String sql = "SELECT id FROM packages WHERE is_sold = FALSE LIMIT 1";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return -1;
    }

    @Override
    public List<Cards> getCardsPackageId(int packageId) throws SQLException {
        String sql = "SELECT cards FROM packages WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, packageId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String cardsJson = resultSet.getString("cards");
                return new ObjectMapper().readValue(cardsJson, new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    @Override
    public void updateCoinsUser(String authToken, int updatedCoins) throws SQLException {
        String sql = "UPDATE users SET coins = ? WHERE token = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, updatedCoins);
            statement.setString(2, authToken);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        }
    }

    @Override
    public void setPackageAsSold(int packageId, int userId) throws SQLException {
        String sql = "UPDATE packages SET is_sold = TRUE, user_id = ? WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, packageId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        }
    }

    @Override
    public boolean addNewPackage(String packageName, List<Cards> cards) throws SQLException {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new SQLException("Package name cannot be null or empty");
        }

        String sql = "INSERT INTO packages (name, is_sold, cards, user_id) VALUES (?, FALSE, ?::jsonb, NULL)";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, packageName);
            String cardsJson = new ObjectMapper().writeValueAsString(cards);
            statement.setString(2, cardsJson);

            boolean result = statement.executeUpdate() > 0;
            if (result) {
                unitOfWork.commitTransaction();
            } else {
                unitOfWork.rollbackTransaction();
            }
            return result;

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw e;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing cards JSON", e);
        }
    }

    @Override
    public Collection<Packages> findPackagesFromToken(String authToken) throws SQLException {
        int userId = userRepository.findUserIdByToken(authToken);
        String sql = """
        SELECT p.id, p.name, p.cards
        FROM packages p
        WHERE p.user_id = ?
        """;
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            Collection<Packages> packages = new ArrayList<>();
            while (resultSet.next()) {
                String cardsJson = resultSet.getString("cards");
                List<Cards> cards = new ObjectMapper().readValue(cardsJson, new TypeReference<List<Cards>>() {});

                packages.add(new Packages(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        cards // Stelle sicher, dass 'cards' eine Liste von 'Cards' ist
                ));

            }
            return packages;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing cards JSON from database", e);
        }
    }

    @Override
    public int countAvailablePackages() throws SQLException {
        String sql = "SELECT COUNT(*) FROM packages WHERE is_sold = FALSE";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0; // Falls keine Pakete vorhanden sind
    }
}
