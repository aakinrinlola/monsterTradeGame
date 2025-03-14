package at.fhtw.monsterTGame.model;

import java.util.List;

public class Packages {

    private int id;
    private String packageName = "Default Package";
    private List<Cards> cards;

    public Packages(int id, String packageName, List<Cards> cards) {
        this.id = id;
        this.packageName = (packageName != null && !packageName.isEmpty()) ? packageName : "Default Package";
        this.cards = cards;
        System.out.println("Package created with name: " + this.packageName);
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
