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
    public User findByName(String name) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setSessionToken(resultSet.getString("session_token"));
                return user;
            }
        } catch (SQLException e) {
            throw new SQLException("Error while finding user by name: " + name, e);
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
                user.setName(resultSet.getString("name"));
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setSessionToken(resultSet.getString("session_token"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalAccessException("Failed to retrieve users. Reason: " + e.getMessage());
        }
    }

    @Override
    public boolean saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, password_hash, session_token) VALUES (?, ?, ?)";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getSessionToken());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while saving user: " + user.getName(), e);
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
    public void updateTocken(String name, String sessionToken) throws SQLException {
        String sql = "UPDATE users SET session_token = ? WHERE name = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, sessionToken);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating session token for user: " + name, e);
        }
    }
}
