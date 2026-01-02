public interface Building {
    String getName();
    int getCost();
    void build();
    void produce();
}

class CommandCenter implements Building {
    private String name = "Command Center";
    private int cost = 500;
    
    @Override
    public String getName() { return name; }
    
    @Override
    public int getCost() { return cost; }
    
    @Override
    public void build() {
        System.out.println("Building Command Center...");
    }
    
    @Override
    public void produce() {
        System.out.println("Command Center: Managing operations");
    }
}