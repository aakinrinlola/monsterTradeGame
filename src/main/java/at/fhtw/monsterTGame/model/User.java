package at.fhtw.monsterTGame.model;

public class User {
    private int userId;
    private String name;
    private String passwordHash;
    private String sessionToken;

    // Standard-Konstruktor
    public User() {}

    // Konstruktor zur Initialisierung
    public User(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.sessionToken = null;
    }

    // Getter und Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
