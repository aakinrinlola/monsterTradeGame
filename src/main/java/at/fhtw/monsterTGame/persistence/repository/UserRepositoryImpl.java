package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.UnitOfWork;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User findName(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("userid"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("token"));
            }
        }catch (SQLException e){
            throw new SQLException("Error finding user by name", e);

        }
        return null;
    }

    @Override
    public User findId(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userid = ?";
        try(PreparedStatement statement = this.unitOfWork.prepareStatement(sql)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User (resultSet.getInt("userid"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("token"));
            }
        } catch (SQLException e){
            throw new SQLException("Error finding user by ID", e);
        }
        return null;
    }

    @Override
    public Collection<User> findAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        try(PreparedStatement statement = this.unitOfWork.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();
            Collection<User> users = new ArrayList<>();
            while (resultSet.next()){
                users.add(new User (resultSet.getInt("userid"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("token")));
            }
            return users;
        } catch (SQLException e){
            throw new SQLException("Error finding users", e);
        }
    }

    @Override
    public boolean saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, token) VALUES (?, ?, ?)";
        try(PreparedStatement statement = this.unitOfWork.prepareStatement(sql)){
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            boolean result = statement.executeUpdate() > 0;
            unitOfWork.commitTransaction();
            return result;
        }catch (SQLException e){
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error saving user", e);
        }

    }

    @Override
    public void updateToken(String username, String token) throws SQLException {
        String sql = "UPDATE users SET token = ? WHERE username = ?";
        try(PreparedStatement statement = this.unitOfWork.prepareStatement(sql)){
            statement.setString(1, token);
            statement.setString(2, username);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        }catch (SQLException e){
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error updating token", e);
        }

    }

    @Override
    public User findToken(String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE token =?";
        try(PreparedStatement statement = this.unitOfWork.prepareStatement(sql)){
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("userid"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("token"));
            }
        }catch (SQLException e){
            throw new SQLException("Error finding token", e);
        }
        return null;
    }

    @Override
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE userid = ?";
        try {PreparedStatement statement = this.unitOfWork.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        }catch (SQLException e){
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error deleting user", e);
        }

    }
}