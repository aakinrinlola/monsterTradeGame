package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;

public class Cards {
    private String cardId;  // Jetzt als String, passend zur DB
    private String name;
    private ElementTypeEnum elementType;
    private CardTypeEnum category;
    private double damage;
    private Integer userId; // Kann null sein

    public Cards() {}

    public Cards(String cardId, String name, ElementTypeEnum elementType, CardTypeEnum category, double damage, Integer userId) {
        this.cardId = cardId;  // Keine Konvertierung zu int!
        this.name = name;
        this.elementType = elementType;
        this.category = category;
        this.damage = damage;
        this.userId = userId;
    }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ElementTypeEnum getElementType() { return elementType; }
    public void setElementType(ElementTypeEnum elementType) { this.elementType = elementType; }

    public CardTypeEnum getCategory() { return category; }
    public void setCategory(CardTypeEnum category) { this.category = category; }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
