public abstract class Character {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attack;
    protected int defense;
    protected int level;
    protected int experience;
    

    protected double posX = 0;
    protected double posY = 0;
    protected double targetX = 0;
    protected double targetY = 0;
    protected double speed = 3.0;
    protected boolean isMoving = false;
    
    public Character(String name, int maxHealth, int attack, int defense) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.level = 1;
        this.experience = 0;
    }
    
    public abstract void attack();
    public abstract void useSpecialAbility();
    
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        health -= actualDamage;
        if (health < 0) health = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    public void gainExperience(int exp) {
        experience += exp;
        if (experience >= 100) {
            levelUp();
        }
    }
    
    public void levelUp() {
        level++;
        experience = 0;
        maxHealth += 20;
        health = maxHealth;
        attack += 5;
        defense += 3;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    

    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
    public void setPosition(double x, double y) { 
        posX = x; 
        posY = y; 
        isMoving = false;
    }
    
    public void setTarget(double x, double y) { 
        targetX = x; 
        targetY = y; 
        isMoving = true; 
    }
    
    public boolean isMoving() { return isMoving; }
    
    public void updatePosition() {
        if (!isMoving) return;
        
        double dx = targetX - posX;
        double dy = targetY - posY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < speed) {
            posX = targetX;
            posY = targetY;
            isMoving = false;
        } else {
            posX += (dx / distance) * speed;
            posY += (dy / distance) * speed;
        }
    }
    
    public double getDistanceTo(double x, double y) {
        double dx = x - posX;
        double dy = y - posY;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    
    public void increaseMaxHealth(int amount) {
        maxHealth += amount;
        health += amount;
    }
    
    public void increaseAttack(int amount) {
        attack += amount;
    }
    
    public void increaseDefense(int amount) {
        defense += amount;
    }
}