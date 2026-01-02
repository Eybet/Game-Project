public class Soldier extends Character {
    
    public Soldier() {
        super("Soldier", 100, 20, 15);
    }
    
    @Override
    public void attack() {
        System.out.println(name + " performs a sword slash!");
        
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Shield Bash! (Stuns enemy)");
        
    }
    
    
    public void charge() {
        System.out.println(name + " charges forward!");
        attack += 10; 
    }
}