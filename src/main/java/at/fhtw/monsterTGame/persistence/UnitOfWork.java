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

    // speichert alle Änderungen an der Datenbank
    public void commitTransaction() {
        if (this.connection != null) {
            try {
                this.connection.commit();
            } catch (SQLException e) {
                throw new DataAccessException("Transaktion konnte nicht abgeschlossen werden (Commit-Fehler).", e);
            }
        }
    }

    // setzt Alle Ändefrugnen zurück wenn Fehler auftritt oder man was abbricht
    public void rollbackTransaction() {
        if (this.connection != null) {
            try {
                this.connection.rollback();
            } catch (SQLException e) {
                throw new DataAccessException("Zurücksetzen der Transaktion fehlgeschlagen (Rollback-Fehler).", e);
            }
        }
    }

    //hier wird die Verbindung geschlossen - Sicherstellung dass keine Ressourcen geleaked werden
    public void finishWork() {
        //wegen der Implementierung von AutoCloseable - kann Verbindung immer geschlossen werden auch bei Ausnahmen
        if (this.connection != null) {
            //das mit try-with nur wegen der AutoCloseable Impl
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                throw new DataAccessException("Fehler: Die Verbindung konnte nicht ordnungsgemäß geschlossen werden.", e);
            }
        }
    }

    //stellt sicher dass man SQL Befehele vorbereitet werden die dann ausgeführt werden
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
