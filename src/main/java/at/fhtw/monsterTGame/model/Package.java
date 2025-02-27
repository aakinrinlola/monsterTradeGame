package at.fhtw.monsterTGame.model;

import java.util.List;

public class Package {
    private int id;  // Falls du UUID nutzen willst -> String id;
    private String packageName;
    private List<String> cardIds;  // Karten als Liste von IDs, um JSONB in DB zu matchen

    public Package(int id, String packageName, List<String> cardIds) {
        this.id = id;
        this.packageName = packageName;
        this.cardIds = cardIds;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }  // Falls UUID -> setId(String id)

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public List<String> getCardIds() { return cardIds; }
    public void setCardIds(List<String> cardIds) { this.cardIds = cardIds; }
}
