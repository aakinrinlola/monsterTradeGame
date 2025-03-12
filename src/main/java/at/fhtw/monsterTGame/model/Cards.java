package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cards {
    private String cardId;
    private String name;
    private ElementTypeEnum elementType;
    private CardTypeEnum category;
    private double damage;
    private Integer userId;

    public Cards() {}

    public Cards(String cardId, String name, ElementTypeEnum elementType, CardTypeEnum category, double damage, Integer userId) {
        this.cardId = cardId;
        this.name = name;
        this.elementType = elementType;
        this.category = category;
        this.damage = damage;
        this.userId = userId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Berechnet den Schaden dieser Karte gegen eine andere Karte unter Berücksichtigung von Elementstärken.
     */
    public double calculateDamageAgainst(Cards opponentCard) {
        if (this.category == CardTypeEnum.MONSTER && opponentCard.category == CardTypeEnum.MONSTER) {
            return this.damage;
        }
        double effectiveness = this.elementType.calculateEffectiveness(opponentCard.elementType);
        return this.damage * effectiveness;
    }

    /**
     * Überprüft, ob diese Karte eine Zauberkarte ist.
     */
    public boolean isSpell() {
        return this.category == CardTypeEnum.SPELL;
    }

    /**
     * Prüft, ob diese Karte gegen eine andere Karte effektiv ist.
     */
    public boolean isEffectiveAgainst(Cards other) {
        return switch (this.elementType) {
            case WATER -> other.elementType == ElementTypeEnum.FIRE;
            case FIRE -> other.elementType == ElementTypeEnum.GRASS;
            case GRASS -> other.elementType == ElementTypeEnum.WATER;
            default -> false;
        };
    }

    /**
     * Prüft, ob diese Karte gegen eine andere Karte ineffektiv ist.
     */
    public boolean isIneffectiveAgainst(Cards other) {
        return switch (this.elementType) {
            case FIRE -> other.elementType == ElementTypeEnum.WATER;
            case GRASS -> other.elementType == ElementTypeEnum.FIRE;
            case WATER -> other.elementType == ElementTypeEnum.GRASS;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return "Cards{" +
                "cardId='" + cardId + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", elementType=" + elementType +
                ", category=" + category +
                ", userId=" + userId +
                '}';
    }
}
