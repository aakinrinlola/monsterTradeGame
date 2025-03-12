package at.fhtw.monsterTGame.model;

import java.util.List;

public class Packages {

    private int id;
    private String packageName;
    private List<Cards> cards; // <-- Ändere hier von List<String> zu List<Cards>

    public Packages(int id, String packageName, List<Cards> cards) {
        this.id = id;
        this.packageName = packageName;
        this.cards = cards;
    }

    public String getName() {
        return packageName;
    }

    public void setName(String packageName) {
        this.packageName = packageName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Cards> getCards() { // <-- Hier auch ändern
        return cards;
    }

    public void setCards(List<Cards> cards) { // <-- Hier auch ändern
        this.cards = cards;
    }
}
