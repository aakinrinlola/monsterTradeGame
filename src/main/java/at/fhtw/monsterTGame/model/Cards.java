package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;

public class Cards {
    private int cardId;
    private String title;
    private ElementTypeEnum elementType;
    private CardTypeEnum category;

    // Standard-Konstruktor
    public Cards() {}

    // Konstruktor mit Parametern
    public Cards(int cardId, String title, ElementTypeEnum elementType, CardTypeEnum category) {
        this.cardId = cardId;
        this.title = title;
        this.elementType = elementType;
        this.category = category;
    }

    // Getter und Setter
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ElementTypeEnum getElementType() {
        return elementType;
    }

    public void setElementType(ElementTypeEnum elementType) {
        this.elementType = elementType;
    }

    public CardTypeEnum getCategory() {
        return category;
    }

    public void setCategory(CardTypeEnum category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Cards{" +
                "cardId=" + cardId +
                ", title='" + title + '\'' +
                ", elementType=" + elementType +
                ", category=" + category +
                '}';
    }
}
