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
                return new User(
                        resultSet.getInt("userid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getInt("elo"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("wins"),    // FEHLTE BISHER
                        resultSet.getInt("losses"),  // FEHLTE BISHER
                        resultSet.getInt("draws")    // FEHLTE BISHER
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding user by name", e);
        }
        return null;
    }


    @Override
    public User findId(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("userid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getInt("elo"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("losses"),
                        resultSet.getInt("draws")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding user by ID", e);
        }
        return null;
    }


    @Override
    public Collection<User> findAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Collection<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("userid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getInt("elo"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("wins"),    // FEHLTE BISHER
                        resultSet.getInt("losses"),  // FEHLTE BISHER
                        resultSet.getInt("draws")    // FEHLTE BISHER
                ));
            }
            return users;
        } catch (SQLException e) {
            throw new SQLException("Error finding users", e);
        }
    }


    @Override
    public boolean saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, token, elo, coins) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getElo());
            statement.setInt(5, user.getCoins());
            boolean result = statement.executeUpdate() > 0;
            unitOfWork.commitTransaction();
            return result;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error saving user", e);
        }
    }

    @Override
    public void updateToken(String username, String token) throws SQLException {
        String sql = "UPDATE users SET token = ? WHERE username = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, token);
            statement.setString(2, username);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error updating token", e);
        }
    }

    @Override
    public User findToken(String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE token = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("userid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getInt("elo"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("wins"),    // FEHLTE BISHER
                        resultSet.getInt("losses"),  // FEHLTE BISHER
                        resultSet.getInt("draws")    // FEHLTE BISHER
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding token", e);
        }
        return null;
    }


    @Override
    public int findUserIdByToken(String authToken) throws SQLException {
        String sql = "SELECT userid FROM users WHERE token = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setString(1, authToken);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding user ID by token", e);
        }
        return -1; // Falls kein User gefunden wurde
    }

    @Override
    public void updateELOAndGamesPlayed(int userId, int eloChange) throws SQLException {
        String sql = "UPDATE users SET elo = elo + ?, games_played = games_played + 1 WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, eloChange);
            statement.setInt(2, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error updating ELO and games played", e);
        }
    }

    @Override
    public void updateCoinsForExtraPrice(int userId, int coins) throws SQLException {
        String sql = "UPDATE users SET coins = coins + ? WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, coins);
            statement.setInt(2, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error updating coins", e);
        }
    }

    @Override
    public void updateWinLossRecord(int userId, boolean won, boolean draw) throws SQLException {
        String sql = "UPDATE users SET wins = wins + ?, losses = losses + ?, draws = draws + ? WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, won ? 1 : 0);
            statement.setInt(2, (!won && !draw) ? 1 : 0);
            statement.setInt(3, draw ? 1 : 0);
            statement.setInt(4, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error updating win/loss record", e);
        }
    }

    @Override
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error deleting user", e);
        }
    }

    @Override
    public List<User> getUsersSortedByELO() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY elo DESC";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("userid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getInt("elo"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("wins"),    // FEHLTE BISHER
                        resultSet.getInt("losses"),  // FEHLTE BISHER
                        resultSet.getInt("draws")    // FEHLTE BISHER
                ));
            }
            return users;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving leaderboard", e);
        }
    }

    @Override
    public void incrementGamesPlayed(int userId) throws SQLException {
        String sql = "UPDATE users SET games_played = games_played + 1 WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Error incrementing games played", e);
        }
    }

    @Override
    public int getUserWins(int userId) throws SQLException {
        String sql = "SELECT wins FROM users WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("wins");
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user wins", e);
        }
        return 0;
    }

    @Override
    public int getUserLosses(int userId) throws SQLException {
        String sql = "SELECT losses FROM users WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("losses");
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user losses", e);
        }
        return 0;
    }

    @Override
    public int getUserDraws(int userId) throws SQLException {
        String sql = "SELECT draws FROM users WHERE userid = ?";
        try (PreparedStatement statement = this.unitOfWork.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("draws");
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user draws", e);
        }
        return 0;
    }

}
