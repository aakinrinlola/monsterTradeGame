package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;

public class Cards {
    private int cardId;
    private String title;
    private ElementTypeEnum elementType;
    private CardTypeEnum category;
    private float damage;
    private int userId; // Verbindung zur User-Tabelle

    public Cards() {}

    public Cards(int cardId, String title, ElementTypeEnum elementType, CardTypeEnum category, float damage, int userId) {
        this.cardId = cardId;
        this.title = title;
        this.elementType = elementType;
        this.category = category;
        this.damage = damage;
        this.userId = userId;
    }

    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ElementTypeEnum getElementType() { return elementType; }
    public void setElementType(ElementTypeEnum elementType) { this.elementType = elementType; }

    public CardTypeEnum getCategory() { return category; }
    public void setCategory(CardTypeEnum category) { this.category = category; }

    public float getDamage() { return damage; }
    public void setDamage(float damage) { this.damage = damage; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
