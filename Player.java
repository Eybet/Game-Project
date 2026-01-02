public class Player {
    private String characterType; 
    private int health;
    private int maxHealth;
    private int attack;
    private int gold;
    private int wood;
    private int stone;
    
    public Player() {
        this.characterType = "soldier";
        this.maxHealth = 100;
        this.health = maxHealth;
        this.attack = 20;
        this.gold = 1000;
        this.wood = 500;
        this.stone = 0;
    }
    
    
    public String getCharacterType() { return characterType; }
    public void setCharacterType(String type) { 
        characterType = type;
        if (type.equals("knight")) {
            maxHealth = 120;
            attack = 25;
        } else {
            maxHealth = 100;
            attack = 20;
        }
        health = maxHealth;
    }
    
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getGold() { return gold; }
    public int getWood() { return wood; }
    public int getStone() { return stone; }
    
    public void setGold(int gold) { this.gold = gold; }
    public void setWood(int wood) { this.wood = wood; }
    
    public void addGold(int amount) { gold += amount; }
    public void addWood(int amount) { wood += amount; }
    public void addStone(int amount) { stone += amount; }
    
    public void increaseMaxHealth(int amount) { maxHealth += amount; health = maxHealth; }
    public void increaseAttack(int amount) { attack += amount; }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    public boolean isAlive() { return health > 0; }
}