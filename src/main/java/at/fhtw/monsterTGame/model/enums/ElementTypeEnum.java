package at.fhtw.monsterTGame.model.enums;

public enum ElementTypeEnum {
    FIRE,
    WATER,
    GRASS;

    // Berechnet die Effektivität des aktuellen Elements gegen das gegnerische Element
    public double calculateEffectiveness(ElementTypeEnum opponent) {
        // Wenn das aktuelle Element Feuer ist
        if (this == FIRE) {
            // Feuer ist stark gegen Gras, also doppelter Schaden
            if (opponent == GRASS) {
                return 2.0;
            }
            // Feuer ist schwach gegen Wasser, also halber Schaden
            if (opponent == WATER) {
                return 0.5;
            }
        }

        // Wenn das aktuelle Element Wasser ist
        if (this == WATER) {
            // Wasser ist stark gegen Feuer, also doppelter Schaden
            if (opponent == FIRE) {
                return 2.0;
            }
            // Wasser ist schwach gegen Gras, also halber Schaden
            if (opponent == GRASS) {
                return 0.5;
            }
        }

        // Wenn das aktuelle Element Gras ist
        if (this == GRASS) {
            // Gras ist stark gegen Wasser, also doppelter Schaden
            if (opponent == WATER) {
                return 2.0;
            }
            // Gras ist schwach gegen Feuer, also halber Schaden
            if (opponent == FIRE) {
                return 0.5;
            }
        }

        // Falls keine besondere Effektivität besteht, normaler Schaden
        return 1.0;
    }
}
