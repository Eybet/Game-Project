import java.util.Random;

public class Game {
    private Player player;
    private String currentLocation;
    private Random random;
    
    public Game() {
        this.player = new Player();
        this.currentLocation = "main_menu";
        this.random = new Random();
    }
    
    public void startNewGame(String characterType) {
        player = new Player();
        player.setCharacterType(characterType);
        player.setGold(1000);
        player.setWood(500);
        currentLocation = "command_center";
    }
    
    public void continueGame() {
        
        startNewGame("soldier");
    }
    
    public void teleportTo(String location) {
        currentLocation = location;
        
        if (location.equals("fight_map")) {
            spawnMonsters();
        }
    }
    
    private void spawnMonsters() {
        
        System.out.println("Monsters spawned in fight area");
    }
    
    public void attackMonster() {
        if (currentLocation.equals("fight_map") || currentLocation.equals("training_camp")) {
            int damage = player.getAttack();
            System.out.println("Player attacks for " + damage + " damage!");
            
            
            if (random.nextInt(100) < 70) {
                String[] loot = {"Gold", "Wood", "Stone"};
                String item = loot[random.nextInt(loot.length)];
                int amount = random.nextInt(50) + 10;
                
                if (item.equals("Gold")) player.addGold(amount);
                if (item.equals("Wood")) player.addWood(amount);
                if (item.equals("Stone")) player.addStone(amount);
                
                System.out.println("Got " + amount + " " + item + "!");
            }
        }
    }
    
    public void upgradeHealth() {
        if (player.getGold() >= 100) {
            player.addGold(-100);
            player.increaseMaxHealth(20);
            System.out.println("Health upgraded! Max HP: " + player.getMaxHealth());
        }
    }
    
    public void upgradeAttack() {
        if (player.getGold() >= 150 && player.getWood() >= 50) {
            player.addGold(-150);
            player.addWood(-50);
            player.increaseAttack(5);
            System.out.println("Attack upgraded! Attack: " + player.getAttack());
        }
    }
    
    
    public Player getPlayer() { return player; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String location) { currentLocation = location; }
}