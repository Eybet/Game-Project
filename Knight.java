public class Knight extends Character {
    
    public Knight() {
        super("Knight", 120, 25, 20);
    }
    
    @Override
    public void attack() {
        System.out.println(name + " swings a heavy sword!");
        
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Whirlwind Attack! (Hits all enemies)");
        
    }
    
    // Knight-specific methods
    public void defend() {
        System.out.println(name + " raises shield! Defense increased.");
        defense += 10;
    }
}