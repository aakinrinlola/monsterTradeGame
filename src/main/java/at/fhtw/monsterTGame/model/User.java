package at.fhtw.monsterTGame.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String token;

    // Standard-Konstruktor
    public User() {}

    // Konstruktor zur Initialisierung bzw Registrierung
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = null;
    }

    //Konstruktor für Datenbankabfrage
    public User(int userId, String username, String password, String token) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
