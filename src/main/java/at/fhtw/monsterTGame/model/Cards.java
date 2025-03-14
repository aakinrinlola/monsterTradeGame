package at.fhtw.monsterTGame.model;

import at.fhtw.monsterTGame.model.enums.CardTypeEnum;
import at.fhtw.monsterTGame.model.enums.ElementTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cards {
    private String card_id;
    private String name;
    private ElementTypeEnum element_type;
    private CardTypeEnum category;
    private double damage;
    private Integer user_id;

    public Cards() {
        this.card_id = UUID.randomUUID().toString();  // Generate a unique card_id
        this.element_type = ElementTypeEnum.WATER;    // Default value
    }

    public Cards(String card_id, String name, ElementTypeEnum element_type, CardTypeEnum category, double damage, Integer user_id) {
        this.card_id = card_id != null ? card_id : UUID.randomUUID().toString();
        this.name = name;
        this.element_type = element_type != null ? element_type : ElementTypeEnum.WATER;
        this.category = category;
        this.damage = damage;
        this.user_id = user_id;
    }

    public String getCardId() {
        return card_id;
    }

    public void setCardId(String card_id) {
        this.card_id = card_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementTypeEnum getElementType() {
        return element_type;
    }

    public void setElementType(ElementTypeEnum element_type) {
        this.element_type = element_type;
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
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public double calculateDamageAgainst(Cards opponentCard) {
        if (this.category == CardTypeEnum.MONSTER && opponentCard.category == CardTypeEnum.MONSTER) {
            return this.damage;
        }
        double effectiveness = this.element_type.calculateEffectiveness(opponentCard.element_type);
        return this.damage * effectiveness;
    }

    public boolean isSpell() {
        return this.category == CardTypeEnum.SPELL;
    }

    public boolean isEffectiveAgainst(Cards other) {
        return switch (this.element_type) {
            case WATER -> other.element_type == ElementTypeEnum.FIRE;
            case FIRE -> other.element_type == ElementTypeEnum.GRASS;
            case GRASS -> other.element_type == ElementTypeEnum.WATER;
            default -> false;
        };
    }

    public boolean isIneffectiveAgainst(Cards other) {
        return switch (this.element_type) {
            case FIRE -> other.element_type == ElementTypeEnum.WATER;
            case GRASS -> other.element_type == ElementTypeEnum.FIRE;
            case WATER -> other.element_type == ElementTypeEnum.GRASS;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return "Cards{" +
                "card_id='" + card_id + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", element_type=" + element_type +
                ", category=" + category +
                ", user_id=" + user_id +
                '}';
    }
}