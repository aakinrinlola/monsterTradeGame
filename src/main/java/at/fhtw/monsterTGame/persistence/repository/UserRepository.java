package at.fhtw.monsterTGame.persistence.repository;

import at.fhtw.monsterTGame.model.User;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface UserRepository {
    // Sucht einen Benutzer nach seinem Benutzernamen
    User findName(String username) throws SQLException;

    // Sucht einen Benutzer nach seiner ID
    User findId(int userId) throws SQLException;

    // Ruft alle Benutzer ab
    Collection<User> findAllUsers() throws SQLException;

    // Speichert einen neuen Benutzer in der Datenbank
    boolean saveUser(User user) throws SQLException;

    // Aktualisiert das Token eines Benutzers (z. B. nach Login)
    void updateToken(String username, String token) throws SQLException;

    // Findet einen Benutzer anhand seines Tokens
    User findToken(String token) throws SQLException;

    // Löscht einen Benutzer basierend auf seiner ID
    void deleteUser(int userId) throws SQLException;

    // Sucht die Benutzer-ID anhand eines Authentifizierungstokens
    int findUserIdByToken(String authToken) throws SQLException;

    // Aktualisiert ELO und Spiele-Statistiken nach einem Kampf
    void updateELOAndGamesPlayed(int userId, int eloChange) throws SQLException;

    // Aktualisiert die Coins eines Benutzers (z. B. nach Belohnungen)
    void updateCoinsForExtraPrice(int userId, int coins) throws SQLException;

    // Aktualisiert die Gewinn- und Verluststatistik eines Benutzers
    void updateWinLossRecord(int userId, boolean won, boolean draw) throws SQLException;

    // Holt eine Liste der Benutzer sortiert nach ELO für das Leaderboard
    List<User> getUsersSortedByELO() throws SQLException;

    // Erhöht die Anzahl der gespielten Spiele für einen Benutzer
    void incrementGamesPlayed(int userId) throws SQLException;

    // Holt die aktuelle Anzahl der Siege eines Benutzers
    int getUserWins(int userId) throws SQLException;

    // Holt die aktuelle Anzahl der Niederlagen eines Benutzers
    int getUserLosses(int userId) throws SQLException;

    // Holt die aktuelle Anzahl der Unentschieden eines Benutzers
    int getUserDraws(int userId) throws SQLException;
}
