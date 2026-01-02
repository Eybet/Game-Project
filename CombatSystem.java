import java.util.Random;

public class CombatSystem {
    private static Random random = new Random();
    
    public static int calculateDamage(int attackerAttack) {
        
        int baseDamage = attackerAttack;
        int variation = random.nextInt(10) - 3; 
        return Math.max(1, baseDamage + variation);
    }
    
    public static boolean tryAttack(int accuracy) {
        
        return random.nextInt(100) < accuracy;
    }
    
    public static String getRandomLoot() {
        String[] loot = {"Gold", "Wood", "Stone", "Health Potion"};
        return loot[random.nextInt(loot.length)];
    }
}