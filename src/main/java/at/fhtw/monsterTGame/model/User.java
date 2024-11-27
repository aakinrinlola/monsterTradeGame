package at.fhtw.monsterTGame.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String sessionToken;

    // Standard-Konstruktor
    public User() {}

    // Konstruktor zur Initialisierung
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.sessionToken = null;
    }

    // Getter und Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
