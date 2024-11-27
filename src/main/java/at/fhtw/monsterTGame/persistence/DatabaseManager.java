package at.fhtw.monsterTGame.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5433/monsterTGame",
                    "mike",
                    ""
            );
        } catch (SQLException e) {
            throw new DataAccessException("Verbindung zur Datenbank konnte nicht hergestellt werden.", e);
        }
    }
}
