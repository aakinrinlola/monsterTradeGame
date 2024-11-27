package at.fhtw.monsterTGame.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnitOfWork implements AutoCloseable {

    private Connection connection;

    public UnitOfWork() {
        this.connection = DatabaseManager.INSTANCE.getConnection();
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("Fehler: Autocommit konnte nicht deaktiviert werden.", e);
        }
    }

    public void commitTransaction() {
        if (this.connection != null) {
            try {
                this.connection.commit();
            } catch (SQLException e) {
                throw new DataAccessException("Transaktion konnte nicht abgeschlossen werden (Commit-Fehler).", e);
            }
        }
    }

    public void rollbackTransaction() {
        if (this.connection != null) {
            try {
                this.connection.rollback();
            } catch (SQLException e) {
                throw new DataAccessException("Zurücksetzen der Transaktion fehlgeschlagen (Rollback-Fehler).", e);
            }
        }
    }

    public void finishWork() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                throw new DataAccessException("Fehler: Die Verbindung konnte nicht ordnungsgemäß geschlossen werden.", e);
            }
        }
    }

    public PreparedStatement prepareStatement(String sql) {
        if (this.connection != null) {
            try {
                return this.connection.prepareStatement(sql);
            } catch (SQLException e) {
                throw new DataAccessException("Fehler beim Erstellen des PreparedStatements.", e);
            }
        }
        throw new DataAccessException("Die UnitOfWork hat keine aktive Verbindung zur Datenbank.");
    }

    @Override
    public void close() throws Exception {
        this.finishWork();
    }
}
