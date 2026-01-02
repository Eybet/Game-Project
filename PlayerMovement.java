
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class PlayerMovement {
    private double targetX = 0;
    private double targetY = 0;
    private double playerX = 0;
    private double playerY = 0;
    private double speed = 5.0;
    private boolean isMoving = false;
    private Circle movementIndicator;
    private AnimationTimer movementTimer;
    
    public PlayerMovement(Pane gameArea) {
        
        movementIndicator = new Circle(5, Color.CYAN);
        movementIndicator.setOpacity(0);
        gameArea.getChildren().add(movementIndicator);
        
        
        movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isMoving) {
                    movePlayer();
                }
            }
        };
        movementTimer.start();
    }
    
    public void handleMouseClick(double x, double y) {
        targetX = x;
        targetY = y;
        isMoving = true;
        
       
        movementIndicator.setCenterX(x);
        movementIndicator.setCenterY(y);
        movementIndicator.setOpacity(1.0);
        

        javafx.animation.Timeline fadeOut = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.5),
                new javafx.animation.KeyValue(movementIndicator.opacityProperty(), 0.0))
        );
        fadeOut.play();
    }
    
    private void movePlayer() {
        double dx = targetX - playerX;
        double dy = targetY - playerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < speed) {
            playerX = targetX;
            playerY = targetY;
            isMoving = false;
        } else {
            playerX += (dx / distance) * speed;
            playerY += (dy / distance) * speed;
        }
    }
    
    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public boolean isMoving() { return isMoving; }
    
    public void stop() {
        if (movementTimer != null) {
            movementTimer.stop();
        }
    }
}