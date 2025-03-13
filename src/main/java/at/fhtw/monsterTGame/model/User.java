package at.fhtw.monsterTGame.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String token;
    private int elo;
    private int coins;
    private int wins;
    private int losses;
    private int draws;

    // Standard-Konstruktor
    public User() {}

    // Konstruktor zur Initialisierung bzw. Registrierung
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = null;
        this.elo = 1000;
        this.coins = 20;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    // Konstruktor für Datenbankabfrage (ohne Wins/Losses/Draws)
    public User(int userId, String username, String password, String token) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.token = token;
        this.elo = 1000;
        this.coins = 20;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    // Konstruktor mit Wins, Losses, Draws für vollständige Datenbankabfrage
    public User(int userId, String username, String password, String token, int elo, int coins, int wins, int losses, int draws) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.token = token;
        this.elo = elo;
        this.coins = coins;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
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

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getGamesWon() {
        return wins;
    }

    public int getGamesLost() {
        return losses;
    }

    public int getGamesDrawn() {
        return draws;
    }

    public void setGamesWon(int wins) {
        this.wins = wins;
    }

    public void setGamesLost(int losses) {
        this.losses = losses;
    }

    public void setGamesDrawn(int draws) {
        this.draws = draws;
    }

    // Berechnet die gespielten Spiele als Summe von Siegen, Niederlagen und Unentschieden
    public int getGamesPlayed() {
        return wins + losses + draws;
    }
}
