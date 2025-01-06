package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepositoryImpl implements UserRepository {
    private final UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, token) VALUES (?, ?, ?)";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getSessionToken());
            int rowsAffected = statement.executeUpdate();
            unitOfWork.commitTransaction(); // Sicherstellen, dass die Transaktion abgeschlossen wird
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Rückgängig machen bei Fehlern
            throw new SQLException("Error while saving user: " + user.getUsername(), e);
        }
    }

    @Override
    public User findByName(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setSessionToken(resultSet.getString("token")); // Ändere hier zu "token"
                return user;
            }
        } catch (SQLException e) {
            throw new SQLException("Error while finding user by name: " + username, e);
        }
        return null;
    }

    @Override
    public Collection<User> findAllUsers() throws IllegalAccessException {
        String sql = "SELECT * FROM users";
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setSessionToken(resultSet.getString("token")); // Ändere hier zu "token"
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalAccessException("Failed to retrieve users. Reason: " + e.getMessage());
        }
    }



    @Override
    public void updateTocken(String username, String sessionToken) throws SQLException {
        String sql = "UPDATE users SET token = ? WHERE username = ?"; // Ändere hier zu "token"
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, sessionToken);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating session token for user: " + username, e);
        }
    }

    @Override
    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, user.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete user with ID: " + user.getUserId() + ". Reason: " + e.getMessage());
        }
    }
    @Override
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setSessionToken(resultSet.getString("token"));
                return user;
            }
        } catch (SQLException e) {
            throw new SQLException("Error while finding user by id: " + userId, e);
        }
        return null;
    }

}
