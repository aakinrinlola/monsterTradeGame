package at.fhtw.monsterTGame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private int id;
    private int userId;
    private List<Cards> cards; // Verwenden der korrekten Klasse

    public Deck(int userId, List<Cards> cards) {
        this.userId = userId;
        this.cards = new ArrayList<>(cards); // Kopie der Liste, um Änderungen von außen zu vermeiden
    }

    public Deck(int id, int userId, List<Cards> cards) {
        this.id = id;
        this.userId = userId;
        this.cards = new ArrayList<>(cards);
    }

    public Deck(List<Cards> cards){
        this.cards = new ArrayList<>(cards);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Cards> getCards() {
        return Collections.unmodifiableList(cards); // Verhindert externe Modifikationen
    }

    public void setCards(List<Cards> cards) {
        this.cards = new ArrayList<>(cards );
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }
}
