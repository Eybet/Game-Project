import java.util.Random;

public class Monster extends Character {
    private String type;
    private boolean isBoss;
    private Random random;
    
    
    public Monster(String name, int health, int attackPower, int defense) {
        super(name, health, attackPower, defense); 
        this.random = new Random();
        this.type = determineType();
        this.isBoss = random.nextDouble() < 0.1; 
        
        if (isBoss) {
            this.health *= 3;
            this.maxHealth = this.health;
            this.attack *= 2;  
            System.out.println("👑 BOSS MONSTER SPAWNED: " + name + "!");
        }
    }
    
    
    public Monster(String name, int health, int attackPower) {
        this(name, health, attackPower, 5 + new Random().nextInt(10));  
    }
    
    private String determineType() {
        String[] types = {"Goblin", "Orc", "Troll", "Dragonkin", "Undead"};
        return types[random.nextInt(types.length)];
    }
    
    @Override
    public void useSpecialAbility() {
        if (isBoss) {
            System.out.println("🔥 " + getName() + " unleashes boss fury!");
            this.attack *= 1.5;  
        } else {
            System.out.println("👹 " + getName() + " uses " + type + " ability!");
            if (random.nextBoolean()) {
                this.attack += 10;  
            } else {
                this.defense += 5;
            }
        }
    }
    
    
    @Override
    public void attack() {
        System.out.println(getName() + " prepares to attack!");
    }
    
    
    public void attackTarget(Character target) {
        int damage = this.attack;  
        if (random.nextDouble() < 0.3) { 
            damage *= 1.5;
            System.out.println("💥 " + getName() + " lands a critical hit!");
        }
        target.takeDamage(damage);
        System.out.println(getName() + " attacks " + target.getName() + " for " + damage + " damage!");
    }
    
    public void roar() {
        System.out.println("🐉 " + getName() + " lets out a terrifying roar!");
    }
    
    public String getType() { return type; }
    public boolean isBoss() { return isBoss; }
    
    @Override
    public String toString() {
        String bossTag = isBoss ? " [BOSS]" : "";
        return String.format("%s%s %s (Lvl %d) HP: %d/%d", 
            type, bossTag, getName(), getLevel(), getHealth(), getMaxHealth());
    }
}