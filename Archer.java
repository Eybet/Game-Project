public class Archer extends Character {
    
    public Archer() {
        super("Archer", 80, 30, 10);
    }
    
    @Override
    public void attack() {
        System.out.println(name + " shoots an arrow!");
        
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Multi-shot! (Hits 3 times)");
        
    }
    
   
    public void snipe() {
        System.out.println(name + " takes careful aim for a critical hit!");
        attack += 15; 
    }
}