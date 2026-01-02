import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameUI {
    private Game game;
    private Stage stage;
    private StackPane root;
    private ImageView background;
    private Label statusLabel;
    private VBox resourcePanel;
    private Random random = new Random();
    private Timeline monsterMovementTimer;
    private Timeline monsterAttackTimer;
    private Timeline victoryTimer;
    private Timeline trainingSlimeMovementTimer;
    
    
    private Image soldierImage;
    private Image knightImage;
    private Image slimeImage;
    private Image skeletonImage;
    private Image goblinImage;
    private Image mapIconImage;
    private Image shopIconImage;
    private Image menuBgImage;
    private Image commandCenterImage;
    private Image fightMapImage;
    private Image trainingCampImage;
    
    
    private StackPane playerPane;
    private double playerX = 0;
    private double playerY = -100;
    private List<MonsterData> monsters = new ArrayList<>();
    private List<TrainingSlimeData> trainingSlimes = new ArrayList<>();
    private boolean isMoving = false;
    private StackPane centerArea;
    private HBox resourcesDisplay;
    private Scene currentScene;
    
    private class MonsterData {
        StackPane pane;
        String type;
        Color color;
        int health;
        int maxHealth;
        boolean alive = true;
        double x, y;
        double targetX, targetY;
        int attackPower;
        boolean isChasing = false;
        ProgressBar healthBar;
        
        MonsterData(StackPane pane, String type, Color color, double x, double y) {
            this.pane = pane;
            this.type = type;
            this.color = color;
            this.x = x;
            this.y = y;
            this.targetX = x;
            this.targetY = y;
            
            switch(type) {
                case "slime":
                    this.maxHealth = 60 + random.nextInt(40);
                    this.attackPower = 5 + random.nextInt(5);
                    break;
                case "skeleton":
                    this.maxHealth = 100 + random.nextInt(60);
                    this.attackPower = 8 + random.nextInt(7);
                    break;
                case "goblin":
                    this.maxHealth = 140 + random.nextInt(80);
                    this.attackPower = 12 + random.nextInt(8);
                    break;
                default:
                    this.maxHealth = 100 + random.nextInt(100);
                    this.attackPower = 10 + random.nextInt(10);
            }
            
            this.health = this.maxHealth;
        }
    }
    
    private class TrainingSlimeData {
        StackPane pane;
        int health;
        int maxHealth;
        boolean alive = true;
        double x, y;
        double targetX, targetY;
        ProgressBar healthBar;
        
        TrainingSlimeData(StackPane pane, double x, double y) {
            this.pane = pane;
            this.x = x;
            this.y = y;
            this.targetX = x;
            this.targetY = y;
            this.maxHealth = 50 + random.nextInt(30);
            this.health = this.maxHealth;
        }
    }
    
    public GameUI(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        loadImages();
    }
    
    private void loadImages() {
        try {
            soldierImage = loadImage("images/characters/soldier.png");
            knightImage = loadImage("images/characters/knight.png");
            
            slimeImage = loadImage("images/monsters/slime.png");
            skeletonImage = loadImage("images/monsters/skeleton.png");
            goblinImage = loadImage("images/monsters/goblin.png");
            
            mapIconImage = loadImage("images/ui/map_icon.png");
            shopIconImage = loadImage("images/ui/shop_icon.png");
            menuBgImage = loadImage("images/ui/menu_bg.jpg");
            
            commandCenterImage = loadImage("images/backgrounds/command_center.jpg");
            fightMapImage = loadImage("images/backgrounds/fight_map.jpg");
            trainingCampImage = loadImage("images/backgrounds/training_camp.jpg");
            
            System.out.println("Images loaded successfully!");
        } catch (Exception e) {
            System.out.println("Some images not found. Using fallback colors.");
        }
    }
    
    private Image loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    private void movePlayerTo(double targetX, double targetY) {
        if (isMoving) return;
        
        isMoving = true;
        
        Circle indicator = new Circle(5, Color.CYAN);
        indicator.setTranslateX(targetX);
        indicator.setTranslateY(targetY);
        centerArea.getChildren().add(indicator);
        
        Timeline indicatorTimer = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            centerArea.getChildren().remove(indicator);
        }));
        indicatorTimer.play();
        
        double distance = Math.sqrt(Math.pow(targetX - playerX, 2) + Math.pow(targetY - playerY, 2));
        double duration = distance / 300.0;
        
        Timeline movement = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(playerPane.translateXProperty(), playerX),
                new KeyValue(playerPane.translateYProperty(), playerY)
            ),
            new KeyFrame(Duration.seconds(duration),
                e -> {
                    playerX = targetX;
                    playerY = targetY;
                    isMoving = false;
                },
                new KeyValue(playerPane.translateXProperty(), targetX),
                new KeyValue(playerPane.translateYProperty(), targetY)
            )
        );
        
        movement.play();
    }
    
    private void attackMonster(MonsterData monsterData) {
        if (!monsterData.alive) return;
        
        int playerDamage = game.getPlayer().getAttack();
        monsterData.health -= playerDamage;
        
        if (monsterData.healthBar != null) {
            double healthPercent = (double) monsterData.health / monsterData.maxHealth;
            monsterData.healthBar.setProgress(healthPercent);
            
            if (healthPercent < 0.3) {
                monsterData.healthBar.setStyle("-fx-accent: red;");
            } else if (healthPercent < 0.6) {
                monsterData.healthBar.setStyle("-fx-accent: orange;");
            }
        }
        
        if (monsterData.pane.getChildren().get(0) instanceof Rectangle) {
            Rectangle rect = (Rectangle) monsterData.pane.getChildren().get(0);
            Color originalColor = (Color) rect.getFill();
            rect.setFill(Color.WHITE);
            
            Timeline flashTimer = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                rect.setFill(originalColor);
            }));
            flashTimer.play();
        }
        
        if (monsterData.health <= 0) {
            killMonster(monsterData);
            
            int goldReward = 25 + random.nextInt(25);
            int woodReward = 15 + random.nextInt(15);
            
            game.getPlayer().addGold(goldReward);
            game.getPlayer().addWood(woodReward);
            
            statusLabel.setText("Killed " + monsterData.type + "! +" + goldReward + " Gold, +" + woodReward + " Wood");
            updateResourceDisplay(resourcesDisplay);
            
            checkVictory();
        } else {
            statusLabel.setText("Hit " + monsterData.type + " for " + playerDamage + " damage! Remaining HP: " + monsterData.health);
        }
    }
    
    private void attackTrainingSlime(TrainingSlimeData slimeData) {
        if (!slimeData.alive) return;
        
        int playerDamage = game.getPlayer().getAttack();
        slimeData.health -= playerDamage;
        
        if (slimeData.healthBar != null) {
            double healthPercent = (double) slimeData.health / slimeData.maxHealth;
            slimeData.healthBar.setProgress(healthPercent);
            
            if (healthPercent < 0.3) {
                slimeData.healthBar.setStyle("-fx-accent: red;");
            } else if (healthPercent < 0.6) {
                slimeData.healthBar.setStyle("-fx-accent: orange;");
            }
        }
        
        if (slimeData.pane.getChildren().get(0) instanceof Rectangle) {
            Rectangle rect = (Rectangle) slimeData.pane.getChildren().get(0);
            Color originalColor = (Color) rect.getFill();
            rect.setFill(Color.WHITE);
            
            Timeline flashTimer = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                rect.setFill(originalColor);
            }));
            flashTimer.play();
        }
        
        if (slimeData.health <= 0) {
            killTrainingSlime(slimeData);
            statusLabel.setText("Training slime destroyed! It will respawn soon.");
            
            game.getPlayer().addGold(5);
            game.getPlayer().addWood(2);
            updateResourceDisplay(resourcesDisplay);
            
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Platform.runLater(() -> {
                        respawnTrainingSlime(slimeData);
                    });
                } catch (InterruptedException e) {}
            }).start();
        } else {
            statusLabel.setText("Hit training slime for " + playerDamage + " damage! Remaining HP: " + slimeData.health);
        }
    }
    
    private void killMonster(MonsterData monsterData) {
        monsterData.alive = false;
        
        Platform.runLater(() -> {
            centerArea.getChildren().remove(monsterData.pane);
        });
        
        monsters.remove(monsterData);
    }
    
    private void killTrainingSlime(TrainingSlimeData slimeData) {
        slimeData.alive = false;
        slimeData.pane.setVisible(false);
        if (slimeData.healthBar != null) {
            slimeData.healthBar.setVisible(false);
        }
    }
    
    private void respawnTrainingSlime(TrainingSlimeData slimeData) {
        slimeData.alive = true;
        slimeData.health = slimeData.maxHealth;
        slimeData.x = random.nextDouble() * 300 - 150;
        slimeData.y = random.nextDouble() * 150 - 75;
        
        slimeData.pane.setTranslateX(slimeData.x);
        slimeData.pane.setTranslateY(slimeData.y);
        slimeData.pane.setVisible(true);
        
        if (slimeData.healthBar != null) {
            slimeData.healthBar.setProgress(1.0);
            slimeData.healthBar.setStyle("-fx-accent: green;");
            slimeData.healthBar.setVisible(true);
        }
        
        statusLabel.setText("Training slime respawned!");
    }
    
    private void checkVictory() {
        if (monsters.isEmpty()) {
            showVictoryScreen();
        }
    }
    
    private void showVictoryScreen() {
        if (monsterMovementTimer != null) {
            monsterMovementTimer.stop();
        }
        if (monsterAttackTimer != null) {
            monsterAttackTimer.stop();
        }
        
        StackPane victoryOverlay = new StackPane();
        victoryOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        VBox victoryBox = new VBox(20);
        victoryBox.setAlignment(Pos.CENTER);
        victoryBox.setStyle("-fx-background-color: rgba(0, 100, 0, 0.9); -fx-padding: 40px; -fx-background-radius: 15px;");
        
        Label victoryTitle = new Label("🎉 VICTORY! 🎉");
        victoryTitle.setFont(Font.font("Arial", 36));
        victoryTitle.setTextFill(Color.GOLD);
        
        Label victoryMessage = new Label("You have defeated all monsters!");
        victoryMessage.setFont(Font.font("Arial", 24));
        victoryMessage.setTextFill(Color.WHITE);
        
        Label rewardMessage = new Label("Rewards: +200 Gold, +100 Wood");
        rewardMessage.setFont(Font.font("Arial", 18));
        rewardMessage.setTextFill(Color.GOLD);
        
        Label returnMessage = new Label("Returning to Command Center in 3 seconds...");
        returnMessage.setFont(Font.font("Arial", 16));
        returnMessage.setTextFill(Color.LIGHTGRAY);
        
        victoryBox.getChildren().addAll(victoryTitle, victoryMessage, rewardMessage, returnMessage);
        victoryOverlay.getChildren().add(victoryBox);
        
        if (centerArea != null) {
            centerArea.getChildren().add(victoryOverlay);
        }
        
        game.getPlayer().addGold(200);
        game.getPlayer().addWood(100);
        updateResourceDisplay(resourcesDisplay);
        
        victoryTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            game.setCurrentLocation("command_center");
            showCommandCenter();
        }));
        victoryTimer.play();
    }
    
    private void startMonsterMovement() {
        if (monsterMovementTimer != null) {
            monsterMovementTimer.stop();
        }
        
        monsterMovementTimer = new Timeline(
            new KeyFrame(Duration.seconds(0.05), e -> {
                for (MonsterData monster : monsters) {
                    if (monster.alive) {
                        updateMonsterMovement(monster);
                    }
                }
            })
        );
        monsterMovementTimer.setCycleCount(Timeline.INDEFINITE);
        monsterMovementTimer.play();
    }
    
    private void updateMonsterMovement(MonsterData monster) {
        double distanceToPlayer = Math.sqrt(
            Math.pow(monster.x - playerX, 2) + 
            Math.pow(monster.y - playerY, 2)
        );
        
        if (distanceToPlayer < 150) {
            monster.isChasing = true;
            monster.targetX = playerX;
            monster.targetY = playerY;
        } else if (monster.isChasing && distanceToPlayer > 200) {
            monster.isChasing = false;
            setRandomMonsterTarget(monster);
        }
        
        if (!monster.isChasing) {
            double distanceToTarget = Math.sqrt(
                Math.pow(monster.x - monster.targetX, 2) + 
                Math.pow(monster.y - monster.targetY, 2)
            );
            
            if (distanceToTarget < 10) {
                setRandomMonsterTarget(monster);
            }
        }
        
        double speed = monster.isChasing ? 1.5 : 0.8;
        
        double dx = monster.targetX - monster.x;
        double dy = monster.targetY - monster.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            monster.x += (dx / distance) * speed;
            monster.y += (dy / distance) * speed;
            
            double maxY = 210;
            monster.y = Math.max(-maxY, Math.min(maxY, monster.y));
            
            monster.pane.setTranslateX(monster.x);
            monster.pane.setTranslateY(monster.y);
        }
    }
    
    private void setRandomMonsterTarget(MonsterData monster) {
        monster.targetX = monster.x + (random.nextDouble() * 100 - 50);
        monster.targetY = monster.y + (random.nextDouble() * 100 - 50);
        
        double maxY = 210;
        monster.targetX = Math.max(-450, Math.min(450, monster.targetX));
        monster.targetY = Math.max(-maxY, Math.min(maxY, monster.targetY));
    }
    
    private void startTrainingSlimeMovement() {
        if (trainingSlimeMovementTimer != null) {
            trainingSlimeMovementTimer.stop();
        }
        
        trainingSlimeMovementTimer = new Timeline(
            new KeyFrame(Duration.seconds(0.05), e -> {
                for (TrainingSlimeData slime : trainingSlimes) {
                    if (slime.alive) {
                        updateTrainingSlimeMovement(slime);
                    }
                }
            })
        );
        trainingSlimeMovementTimer.setCycleCount(Timeline.INDEFINITE);
        trainingSlimeMovementTimer.play();
    }
    
    private void updateTrainingSlimeMovement(TrainingSlimeData slime) {
        double distanceToTarget = Math.sqrt(
            Math.pow(slime.x - slime.targetX, 2) + 
            Math.pow(slime.y - slime.targetY, 2)
        );
        
        if (distanceToTarget < 10) {
            setRandomTrainingSlimeTarget(slime);
        }
        
        double speed = 0.5;
        
        double dx = slime.targetX - slime.x;
        double dy = slime.targetY - slime.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            slime.x += (dx / distance) * speed;
            slime.y += (dy / distance) * speed;
            
            slime.pane.setTranslateX(slime.x);
            slime.pane.setTranslateY(slime.y);
        }
    }
    
    private void setRandomTrainingSlimeTarget(TrainingSlimeData slime) {
        slime.targetX = slime.x + (random.nextDouble() * 80 - 40);
        slime.targetY = slime.y + (random.nextDouble() * 80 - 40);
        
        slime.targetX = Math.max(-200, Math.min(200, slime.targetX));
        slime.targetY = Math.max(-150, Math.min(150, slime.targetY));
    }
    
    private void startMonsterAttackTimer() {
        if (monsterAttackTimer != null) {
            monsterAttackTimer.stop();
        }
        
        monsterAttackTimer = new Timeline(
            new KeyFrame(Duration.seconds(1.0), e -> {
                for (MonsterData monster : monsters) {
                    if (monster.alive) {
                        checkMonsterAttack(monster);
                    }
                }
            })
        );
        monsterAttackTimer.setCycleCount(Timeline.INDEFINITE);
        monsterAttackTimer.play();
    }
    
    private void checkMonsterAttack(MonsterData monster) {
        double distanceToPlayer = Math.sqrt(
            Math.pow(monster.x - playerX, 2) + 
            Math.pow(monster.y - playerY, 2)
        );
        
        if (distanceToPlayer < 60 && monster.alive) {
            int monsterDamage = monster.attackPower;
            game.getPlayer().takeDamage(monsterDamage);
            updateResourceDisplay(resourcesDisplay);
            
            if (playerPane.getChildren().get(0) instanceof Rectangle) {
                Rectangle playerRect = (Rectangle) playerPane.getChildren().get(0);
                Color originalColor = (Color) playerRect.getFill();
                playerRect.setFill(Color.WHITE);
                
                Timeline flashTimer = new Timeline(new KeyFrame(Duration.millis(200), e -> {
                    playerRect.setFill(originalColor);
                }));
                flashTimer.play();
            }
            
            Platform.runLater(() -> {
                statusLabel.setText(monster.type + " attacked you for " + monsterDamage + " damage!");
            });
            
            if (game.getPlayer().getHealth() <= 0) {
                Platform.runLater(() -> {
                    statusLabel.setText("You were defeated by " + monster.type + "! Returning to command center.");
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> {
                                game.setCurrentLocation("command_center");
                                showCommandCenter();
                            });
                        } catch (InterruptedException e) {}
                    }).start();
                });
            }
        }
    }
    
    private StackPane createMonster(String monsterType, double x, double y) {
        StackPane monsterPane = new StackPane();
        
        Image monsterImage = null;
        Color monsterColor = Color.RED;
        
        switch(monsterType) {
            case "slime":
                monsterImage = slimeImage;
                monsterColor = Color.GREEN;
                break;
            case "skeleton":
                monsterImage = skeletonImage;
                monsterColor = Color.WHITE;
                break;
            case "goblin":
                monsterImage = goblinImage;
                monsterColor = Color.ORANGE;
                break;
        }
        
        if (monsterImage != null) {
            ImageView monsterView = new ImageView(monsterImage);
            monsterView.setFitWidth(50);
            monsterView.setFitHeight(50);
            monsterPane.getChildren().add(monsterView);
        } else {
            Rectangle monsterRect = new Rectangle(50, 50);
            monsterRect.setFill(monsterColor);
            monsterPane.getChildren().add(monsterRect);
        }
        
        ProgressBar healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(50);
        healthBar.setPrefHeight(6);
        healthBar.setTranslateY(30);
        healthBar.setStyle("-fx-accent: green;");
        monsterPane.getChildren().add(healthBar);
        
        monsterPane.setTranslateX(x);
        monsterPane.setTranslateY(y);
        
        return monsterPane;
    }
    
    private StackPane createTrainingSlime(double x, double y) {
        StackPane slimePane = new StackPane();
        
        if (slimeImage != null) {
            ImageView slimeView = new ImageView(slimeImage);
            slimeView.setFitWidth(50);
            slimeView.setFitHeight(50);
            slimePane.getChildren().add(slimeView);
        } else {
            Rectangle slimeRect = new Rectangle(50, 50);
            slimeRect.setFill(Color.GREEN);
            slimePane.getChildren().add(slimeRect);
        }
        
        ProgressBar healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(50);
        healthBar.setPrefHeight(6);
        healthBar.setTranslateY(30);
        healthBar.setStyle("-fx-accent: green;");
        slimePane.getChildren().add(healthBar);
        
        slimePane.setTranslateX(x);
        slimePane.setTranslateY(y);
        
        return slimePane;
    }
    
    private void setupKeyboardControls(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.E) {
                attackNearestMonster();
            }
        });
    }
    
    private void attackNearestMonster() {
        if (monsters.isEmpty()) {
            statusLabel.setText("No monsters to attack!");
            return;
        }
        
        MonsterData nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (MonsterData monster : monsters) {
            if (monster.alive) {
                double distance = Math.sqrt(
                    Math.pow(monster.x - playerX, 2) + 
                    Math.pow(monster.y - playerY, 2)
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = monster;
                }
            }
        }
        
        if (nearest != null) {
            if (minDistance < 100) {
                attackMonster(nearest);
            } else {
                statusLabel.setText("Too far to attack! Get closer to the monster.");
            }
        } else {
            statusLabel.setText("No monsters nearby!");
        }
    }
    
    private void attackNearestTrainingSlime() {
        if (trainingSlimes.isEmpty()) {
            statusLabel.setText("No training slimes to attack!");
            return;
        }
        
        TrainingSlimeData nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (TrainingSlimeData slime : trainingSlimes) {
            if (slime.alive) {
                double distance = Math.sqrt(
                    Math.pow(slime.x - 0, 2) + 
                    Math.pow(slime.y - 0, 2)
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = slime;
                }
            }
        }
        
        if (nearest != null) {
            attackTrainingSlime(nearest);
        } else {
            statusLabel.setText("No training slimes nearby!");
        }
    }
    
    public void showMainMenu() {
        root = new StackPane();
        
        if (menuBgImage != null) {
            background = new ImageView(menuBgImage);
            background.setFitWidth(1200);
            background.setFitHeight(800);
            root.getChildren().add(background);
        } else {
            try {
                Image bg = new Image("file:images/menu_bg.jpg");
                background = new ImageView(bg);
                background.setFitWidth(1200);
                background.setFitHeight(800);
                root.getChildren().add(background);
            } catch (Exception e) {
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
            }
        }
        
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 40px; -fx-background-radius: 15px;");
        
        Label title = new Label("STRATEGY GAME");
        title.setFont(Font.font("Arial", 36));
        title.setTextFill(Color.GOLD);
        
        Button continueBtn = createMenuButton("CONTINUE GAME");
        Button newGameBtn = createMenuButton("START NEW GAME");
        Button exitBtn = createMenuButton("EXIT");
        
        continueBtn.setOnAction(e -> {
            game.continueGame();
            showCharacterSelect();
        });
        
        newGameBtn.setOnAction(e -> {
            showCharacterSelect();
        });
        
        exitBtn.setOnAction(e -> Platform.exit());
        
        menuBox.getChildren().addAll(title, continueBtn, newGameBtn, exitBtn);
        root.getChildren().add(menuBox);
        
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Strategy Game");
        stage.show();
    }
    
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; " +
                    "-fx-padding: 15px 30px; -fx-border-radius: 8px; -fx-cursor: hand;");
        btn.setPrefWidth(300);
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 18px; " +
                                               "-fx-padding: 15px 30px; -fx-border-radius: 8px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; " +
                                              "-fx-padding: 15px 30px; -fx-border-radius: 8px; -fx-cursor: hand;"));
        return btn;
    }
    
    private void showCharacterSelect() {
        VBox selectBox = new VBox(20);
        selectBox.setAlignment(Pos.CENTER);
        selectBox.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 40px; -fx-background-radius: 15px;");
        
        Label title = new Label("SELECT YOUR CHARACTER");
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.WHITE);
        
        HBox characterBox = new HBox(40);
        characterBox.setAlignment(Pos.CENTER);
        
        VBox soldierOption = new VBox(10);
        soldierOption.setAlignment(Pos.CENTER);
        
        StackPane soldierPane = new StackPane();
        if (soldierImage != null) {
            ImageView soldierView = new ImageView(soldierImage);
            soldierView.setFitWidth(100);
            soldierView.setFitHeight(100);
            soldierPane.getChildren().add(soldierView);
        } else {
            Rectangle soldierImg = new Rectangle(100, 100);
            soldierImg.setFill(Color.RED);
            soldierImg.setStroke(Color.WHITE);
            soldierImg.setStrokeWidth(2);
            soldierPane.getChildren().add(soldierImg);
        }
        
        Label soldierLabel = new Label("SOLDIER");
        soldierLabel.setTextFill(Color.WHITE);
        soldierLabel.setFont(Font.font("Arial", 16));
        
        Label soldierStats = new Label("HP: 100 | ATK: 20");
        soldierStats.setTextFill(Color.LIGHTGRAY);
        
        Button selectSoldier = new Button("SELECT");
        selectSoldier.setOnAction(e -> {
            game.startNewGame("soldier");
            showCommandCenter();
        });
        
        soldierOption.getChildren().addAll(soldierPane, soldierLabel, soldierStats, selectSoldier);
        
        VBox knightOption = new VBox(10);
        knightOption.setAlignment(Pos.CENTER);
        
        StackPane knightPane = new StackPane();
        if (knightImage != null) {
            ImageView knightView = new ImageView(knightImage);
            knightView.setFitWidth(100);
            knightView.setFitHeight(100);
            knightPane.getChildren().add(knightView);
        } else {
            Rectangle knightImg = new Rectangle(100, 100);
            knightImg.setFill(Color.BLUE);
            knightImg.setStroke(Color.WHITE);
            knightImg.setStrokeWidth(2);
            knightPane.getChildren().add(knightImg);
        }
        
        Label knightLabel = new Label("KNIGHT");
        knightLabel.setTextFill(Color.WHITE);
        
        Label knightStats = new Label("HP: 120 | ATK: 25");
        knightStats.setTextFill(Color.LIGHTGRAY);
        
        Button selectKnight = new Button("SELECT");
        selectKnight.setOnAction(e -> {
            game.startNewGame("knight");
            showCommandCenter();
        });
        
        knightOption.getChildren().addAll(knightPane, knightLabel, knightStats, selectKnight);
        
        characterBox.getChildren().addAll(soldierOption, knightOption);
        
        Button backBtn = new Button("BACK");
        backBtn.setOnAction(e -> showMainMenu());
        
        selectBox.getChildren().addAll(title, characterBox, backBtn);
        
        root.getChildren().clear();
        if (background != null) root.getChildren().add(background);
        root.getChildren().add(selectBox);
    }
    
    private void showCommandCenter() {
        if (monsterMovementTimer != null) {
            monsterMovementTimer.stop();
        }
        if (monsterAttackTimer != null) {
            monsterAttackTimer.stop();
        }
        if (victoryTimer != null) {
            victoryTimer.stop();
        }
        if (trainingSlimeMovementTimer != null) {
            trainingSlimeMovementTimer.stop();
        }
        
        BorderPane commandCenter = new BorderPane();
        commandCenter.setStyle("-fx-background-color: #2b2b2b;");
        
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #3c3c3c;");
        
        statusLabel = new Label("Command Center - Welcome Commander!");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", 14));
        
        HBox resources = new HBox(15);
        updateResourceDisplay(resources);
        
        topBar.getChildren().addAll(statusLabel, resources);
        commandCenter.setTop(topBar);
        
        StackPane centerArea = new StackPane();
        centerArea.setStyle("-fx-background-color: #1a1a2e;");
        
        if (commandCenterImage != null) {
            ImageView ccView = new ImageView(commandCenterImage);
            ccView.setFitWidth(800);
            ccView.setFitHeight(500);
            centerArea.getChildren().add(ccView);
        } else {
            Rectangle ccRect = new Rectangle(800, 500);
            ccRect.setFill(Color.DARKGRAY);
            ccRect.setStroke(Color.GOLD);
            ccRect.setStrokeWidth(3);
            
            Label ccLabel = new Label("COMMAND CENTER");
            ccLabel.setTextFill(Color.WHITE);
            ccLabel.setFont(Font.font("Arial", 24));
            
            centerArea.getChildren().addAll(ccRect, ccLabel);
        }
        
        HBox actionButtons = new HBox(30);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setTranslateY(100);
        
        Button mapBtn = createIconButton("MAP", "images/map_icon.png");
        mapBtn.setOnAction(e -> showTeleportMenu());
        
        Button shopBtn = createIconButton("SHOP", "images/shop_icon.png");
        shopBtn.setOnAction(e -> showShop());
        
        actionButtons.getChildren().addAll(mapBtn, shopBtn);
        centerArea.getChildren().add(actionButtons);
        
        commandCenter.setCenter(centerArea);
        
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setStyle("-fx-background-color: #3c3c3c;");
        
        Button menuBtn = new Button("MAIN MENU");
        menuBtn.setOnAction(e -> showMainMenu());
        
        bottomBar.getChildren().add(menuBtn);
        commandCenter.setBottom(bottomBar);
        
        Scene scene = new Scene(commandCenter, 1200, 800);
        stage.setScene(scene);
    }
    
    private Button createIconButton(String text, String imagePath) {
        VBox iconBox = new VBox(5);
        iconBox.setAlignment(Pos.CENTER);
        
        Image iconImage = null;
        if (imagePath.contains("map_icon") && mapIconImage != null) {
            iconImage = mapIconImage;
        } else if (imagePath.contains("shop_icon") && shopIconImage != null) {
            iconImage = shopIconImage;
        }
        
        if (iconImage != null) {
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconBox.getChildren().add(iconView);
        } else {
            try {
                Image icon = new Image("file:" + imagePath);
                ImageView iconView = new ImageView(icon);
                iconView.setFitWidth(64);
                iconView.setFitHeight(64);
                iconBox.getChildren().add(iconView);
            } catch (Exception e) {
                Rectangle iconRect = new Rectangle(64, 64);
                iconRect.setFill(Color.GRAY);
                iconBox.getChildren().add(iconRect);
            }
        }
        
        Label iconLabel = new Label(text);
        iconLabel.setTextFill(Color.WHITE);
        
        iconBox.getChildren().add(iconLabel);
        
        Button btn = new Button();
        btn.setGraphic(iconBox);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));
        
        return btn;
    }
    
    private void showTeleportMenu() {
        VBox teleportMenu = new VBox(20);
        teleportMenu.setAlignment(Pos.CENTER);
        teleportMenu.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-padding: 40px;");
        
        Label title = new Label("TELEPORT MENU");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.CYAN);
        
        Button fightAreaBtn = createTeleportButton("FIGHT AREA", "Battle monsters and collect resources");
        fightAreaBtn.setOnAction(e -> {
            game.teleportTo("fight_map");
            showFightArea();
        });
        
        Button trainingBtn = createTeleportButton("TRAINING CAMP", "Practice without risk");
        trainingBtn.setOnAction(e -> {
            game.teleportTo("training_camp");
            showTrainingCamp();
        });
        
        Button backBtn = new Button("BACK TO COMMAND CENTER");
        backBtn.setOnAction(e -> showCommandCenter());
        
        teleportMenu.getChildren().addAll(title, fightAreaBtn, trainingBtn, backBtn);
        
        StackPane overlay = new StackPane();
        overlay.getChildren().addAll(root, teleportMenu);
        
        Scene scene = new Scene(overlay, 1200, 800);
        stage.setScene(scene);
    }
    
    private Button createTeleportButton(String name, String description) {
        VBox btnContent = new VBox(5);
        btnContent.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", 18));
        nameLabel.setTextFill(Color.WHITE);
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setTextFill(Color.LIGHTGRAY);
        
        btnContent.getChildren().addAll(nameLabel, descLabel);
        
        Button btn = new Button();
        btn.setGraphic(btnContent);
        btn.setStyle("-fx-background-color: #444; -fx-padding: 15px; -fx-pref-width: 400px;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #555; -fx-padding: 15px; -fx-pref-width: 400px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #444; -fx-padding: 15px; -fx-pref-width: 400px;"));
        
        return btn;
    }
    
    private void showFightArea() {
        BorderPane fightArea = new BorderPane();
        fightArea.setStyle("-fx-background-color: #1a1a1a;");
        
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #333;");
        
        statusLabel = new Label("FIGHT AREA - LEFT CLICK monsters or press 'E' to attack! Monsters have DOUBLE health!");
        statusLabel.setTextFill(Color.WHITE);
        
        HBox resources = new HBox(15);
        updateResourceDisplay(resources);
        resourcesDisplay = resources;
        
        topBar.getChildren().addAll(statusLabel, resources);
        fightArea.setTop(topBar);
        
        centerArea = new StackPane();
        
        if (fightMapImage != null) {
            ImageView mapView = new ImageView(fightMapImage);
            mapView.setFitWidth(1000);
            mapView.setFitHeight(600);
            centerArea.getChildren().add(mapView);
        } else {
            try {
                Image mapImage = new Image("file:images/fight_map.png");
                ImageView mapView = new ImageView(mapImage);
                mapView.setFitWidth(1000);
                mapView.setFitHeight(600);
                centerArea.getChildren().add(mapView);
            } catch (Exception e) {
                Rectangle mapRect = new Rectangle(1000, 600);
                mapRect.setFill(Color.DARKGREEN);
                centerArea.getChildren().add(mapRect);
            }
        }
        
        monsters.clear();
        
        String[] monsterTypes = {"slime", "skeleton", "goblin"};
        double maxY = 210;
        
        for (int i = 0; i < 6; i++) {
            String monsterType = monsterTypes[random.nextInt(monsterTypes.length)];
            Color monsterColor = Color.GREEN;
            switch(monsterType) {
                case "skeleton": monsterColor = Color.WHITE; break;
                case "goblin": monsterColor = Color.ORANGE; break;
            }
            
            double x = random.nextDouble() * 800 - 400;
            double y = random.nextDouble() * (maxY * 2) - maxY;
            
            StackPane monsterPane = createMonster(monsterType, x, y);
            MonsterData monsterData = new MonsterData(monsterPane, monsterType, monsterColor, x, y);
            
            for (javafx.scene.Node node : monsterPane.getChildren()) {
                if (node instanceof ProgressBar) {
                    monsterData.healthBar = (ProgressBar) node;
                    break;
                }
            }
            
            monsters.add(monsterData);
            
            final MonsterData finalMonsterData = monsterData;
            
            monsterPane.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    attackMonster(finalMonsterData);
                }
            });
            
            monsterPane.setOnMouseEntered(e -> {
                if (monsterPane.getChildren().get(0) instanceof Rectangle) {
                    ((Rectangle)monsterPane.getChildren().get(0)).setStroke(Color.YELLOW);
                }
            });
            
            monsterPane.setOnMouseExited(e -> {
                if (monsterPane.getChildren().get(0) instanceof Rectangle) {
                    ((Rectangle)monsterPane.getChildren().get(0)).setStroke(null);
                }
            });
            
            centerArea.getChildren().add(monsterPane);
        }
        
        playerPane = new StackPane();
        if (game.getPlayer().getCharacterType().equals("soldier") && soldierImage != null) {
            ImageView playerView = new ImageView(soldierImage);
            playerView.setFitWidth(50);
            playerView.setFitHeight(50);
            playerPane.getChildren().add(playerView);
        } else if (game.getPlayer().getCharacterType().equals("knight") && knightImage != null) {
            ImageView playerView = new ImageView(knightImage);
            playerView.setFitWidth(50);
            playerView.setFitHeight(50);
            playerPane.getChildren().add(playerView);
        } else {
            Rectangle playerChar = new Rectangle(50, 50);
            playerChar.setFill(game.getPlayer().getCharacterType().equals("soldier") ? Color.RED : Color.BLUE);
            playerPane.getChildren().add(playerChar);
        }
        
        playerX = 0;
        playerY = -100;
        playerPane.setTranslateX(playerX);
        playerPane.setTranslateY(playerY);
        centerArea.getChildren().add(playerPane);
        
        Label playerHealthLabel = new Label("HP: " + game.getPlayer().getHealth() + "/" + game.getPlayer().getMaxHealth());
        playerHealthLabel.setTextFill(Color.WHITE);
        playerHealthLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px;");
        playerHealthLabel.setTranslateY(-130);
        centerArea.getChildren().add(playerHealthLabel);
        
        Timeline healthUpdateTimer = new Timeline(
            new KeyFrame(Duration.seconds(0.5), e -> {
                playerHealthLabel.setText("HP: " + game.getPlayer().getHealth() + "/" + game.getPlayer().getMaxHealth());
                if (game.getPlayer().getHealth() < game.getPlayer().getMaxHealth() * 0.3) {
                    playerHealthLabel.setTextFill(Color.RED);
                } else if (game.getPlayer().getHealth() < game.getPlayer().getMaxHealth() * 0.6) {
                    playerHealthLabel.setTextFill(Color.ORANGE);
                } else {
                    playerHealthLabel.setTextFill(Color.GREEN);
                }
            })
        );
        healthUpdateTimer.setCycleCount(Timeline.INDEFINITE);
        healthUpdateTimer.play();
        
        centerArea.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                double targetX = e.getX() - centerArea.getWidth()/2;
                double targetY = e.getY() - centerArea.getHeight()/2;
                
                targetX = Math.max(-450, Math.min(450, targetX));
                targetY = Math.max(-250, Math.min(250, targetY));
                
                movePlayerTo(targetX, targetY);
                statusLabel.setText("Moving to new position...");
            }
        });
        
        fightArea.setCenter(centerArea);
        
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        
        Button attackBtn = new Button("ATTACK NEAREST (E)");
        attackBtn.setOnAction(e -> {
            attackNearestMonster();
        });
        
        Button teleportBtn = new Button("BACK TO COMMAND CENTER");
        teleportBtn.setOnAction(e -> {
            if (monsterMovementTimer != null) {
                monsterMovementTimer.stop();
            }
            if (monsterAttackTimer != null) {
                monsterAttackTimer.stop();
            }
            healthUpdateTimer.stop();
            
            game.setCurrentLocation("command_center");
            showCommandCenter();
        });
        
        bottomBar.getChildren().addAll(attackBtn, teleportBtn);
        fightArea.setBottom(bottomBar);
        
        Scene scene = new Scene(fightArea, 1200, 800);
        currentScene = scene;
        stage.setScene(scene);
        
        setupKeyboardControls(scene);
        
        startMonsterMovement();
        startMonsterAttackTimer();
    }
    
    private void showTrainingCamp() {
        BorderPane trainingArea = new BorderPane();
        trainingArea.setStyle("-fx-background-color: #1a1a1a;");
        
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #333;");
        
        statusLabel = new Label("TRAINING CAMP - LEFT CLICK slimes or press 'E' to attack! Right-click to move!");
        statusLabel.setTextFill(Color.WHITE);
        
        HBox resources = new HBox(15);
        updateResourceDisplay(resources);
        resourcesDisplay = resources;
        
        topBar.getChildren().addAll(statusLabel, resources);
        trainingArea.setTop(topBar);
        
        StackPane center = new StackPane();
        
        if (trainingCampImage != null) {
            ImageView trainingView = new ImageView(trainingCampImage);
            trainingView.setFitWidth(1000);
            trainingView.setFitHeight(600);
            center.getChildren().add(trainingView);
        } else {
            try {
                Image trainingImage = new Image("file:images/training_camp.png");
                ImageView trainingView = new ImageView(trainingImage);
                trainingView.setFitWidth(1000);
                trainingView.setFitHeight(600);
                center.getChildren().add(trainingView);
            } catch (Exception e) {
                Rectangle trainingRect = new Rectangle(1000, 600);
                trainingRect.setFill(Color.DARKBLUE);
                center.getChildren().add(trainingRect);
            }
        }
        
        trainingSlimes.clear();
        
        for (int i = 0; i < 4; i++) {
            double x = random.nextDouble() * 300 - 150;
            double y = random.nextDouble() * 150 - 75;
            
            StackPane slimePane = createTrainingSlime(x, y);
            TrainingSlimeData slimeData = new TrainingSlimeData(slimePane, x, y);
            
            for (javafx.scene.Node node : slimePane.getChildren()) {
                if (node instanceof ProgressBar) {
                    slimeData.healthBar = (ProgressBar) node;
                    break;
                }
            }
            
            trainingSlimes.add(slimeData);
            
            final TrainingSlimeData finalSlimeData = slimeData;
            
            slimePane.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    attackTrainingSlime(finalSlimeData);
                }
            });
            
            slimePane.setOnMouseEntered(e -> {
                if (slimePane.getChildren().get(0) instanceof Rectangle) {
                    ((Rectangle)slimePane.getChildren().get(0)).setStroke(Color.YELLOW);
                }
            });
            
            slimePane.setOnMouseExited(e -> {
                if (slimePane.getChildren().get(0) instanceof Rectangle) {
                    ((Rectangle)slimePane.getChildren().get(0)).setStroke(null);
                }
            });
            
            center.getChildren().add(slimePane);
        }
        
        StackPane trainingPlayer = new StackPane();
        if (game.getPlayer().getCharacterType().equals("soldier") && soldierImage != null) {
            ImageView playerView = new ImageView(soldierImage);
            playerView.setFitWidth(50);
            playerView.setFitHeight(50);
            trainingPlayer.getChildren().add(playerView);
        } else if (game.getPlayer().getCharacterType().equals("knight") && knightImage != null) {
            ImageView playerView = new ImageView(knightImage);
            playerView.setFitWidth(50);
            playerView.setFitHeight(50);
            trainingPlayer.getChildren().add(playerView);
        } else {
            Rectangle playerChar = new Rectangle(50, 50);
            playerChar.setFill(game.getPlayer().getCharacterType().equals("soldier") ? Color.RED : Color.BLUE);
            trainingPlayer.getChildren().add(playerChar);
        }
        
        trainingPlayer.setTranslateX(0);
        trainingPlayer.setTranslateY(0);
        center.getChildren().add(trainingPlayer);
        
        center.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.E) {
                attackNearestTrainingSlime();
            }
        });
        
        center.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                double targetX = e.getX() - center.getWidth()/2;
                double targetY = e.getY() - center.getHeight()/2;
                
                targetX = Math.max(-200, Math.min(200, targetX));
                targetY = Math.max(-150, Math.min(150, targetY));
                
                Circle indicator = new Circle(5, Color.CYAN);
                indicator.setTranslateX(targetX);
                indicator.setTranslateY(targetY);
                center.getChildren().add(indicator);
                
                Timeline indicatorTimer = new Timeline(new KeyFrame(Duration.seconds(0.5), ev -> {
                    center.getChildren().remove(indicator);
                }));
                indicatorTimer.play();
                
                trainingPlayer.setTranslateX(targetX);
                trainingPlayer.setTranslateY(targetY);
                statusLabel.setText("Moving to training position...");
            }
        });
        
        center.setFocusTraversable(true);
        center.requestFocus();
        
        trainingArea.setCenter(center);
        
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        
        Button attackBtn = new Button("ATTACK SLIME (E)");
        attackBtn.setOnAction(e -> {
            attackNearestTrainingSlime();
        });
        
        Button teleportBtn = new Button("BACK TO COMMAND CENTER");
        teleportBtn.setOnAction(e -> {
            if (trainingSlimeMovementTimer != null) {
                trainingSlimeMovementTimer.stop();
            }
            
            game.setCurrentLocation("command_center");
            showCommandCenter();
        });
        
        bottomBar.getChildren().addAll(attackBtn, teleportBtn);
        trainingArea.setBottom(bottomBar);
        
        Scene scene = new Scene(trainingArea, 1200, 800);
        stage.setScene(scene);
        
        startTrainingSlimeMovement();
    }
    
    private void showShop() {
        VBox shopMenu = new VBox(20);
        shopMenu.setAlignment(Pos.CENTER);
        shopMenu.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-padding: 40px;");
        
        Label title = new Label("UPGRADE SHOP");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.GOLD);
        
        Player player = game.getPlayer();
        
        VBox upgrades = new VBox(15);
        
        HBox healthUpgrade = new HBox(20);
        healthUpgrade.setAlignment(Pos.CENTER_LEFT);
        
        Label healthLabel = new Label("Upgrade Health (+20 HP)");
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setPrefWidth(200);
        
        Label healthCost = new Label("Cost: 100 Gold");
        healthCost.setTextFill(Color.GOLD);
        
        Button buyHealth = new Button("BUY");
        buyHealth.setOnAction(e -> {
            game.upgradeHealth();
            updateShop(shopMenu);
        });
        
        healthUpgrade.getChildren().addAll(healthLabel, healthCost, buyHealth);
        
        HBox attackUpgrade = new HBox(20);
        attackUpgrade.setAlignment(Pos.CENTER_LEFT);
        
        Label attackLabel = new Label("Upgrade Attack (+5 ATK)");
        attackLabel.setTextFill(Color.WHITE);
        attackLabel.setPrefWidth(200);
        
        Label attackCost = new Label("Cost: 150 Gold, 50 Wood");
        attackCost.setTextFill(Color.GOLD);
        
        Button buyAttack = new Button("BUY");
        buyAttack.setOnAction(e -> {
            game.upgradeAttack();
            updateShop(shopMenu);
        });
        
        attackUpgrade.getChildren().addAll(attackLabel, attackCost, buyAttack);
        
        upgrades.getChildren().addAll(healthUpgrade, attackUpgrade);
        
        VBox playerStats = new VBox(5);
        playerStats.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15px;");
        
        Label statsTitle = new Label("YOUR STATS");
        statsTitle.setTextFill(Color.CYAN);
        
        Label healthStat = new Label("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        healthStat.setTextFill(Color.WHITE);
        
        Label attackStat = new Label("Attack: " + player.getAttack());
        attackStat.setTextFill(Color.WHITE);
        
        Label goldStat = new Label("Gold: " + player.getGold());
        goldStat.setTextFill(Color.GOLD);
        
        Label woodStat = new Label("Wood: " + player.getWood());
        woodStat.setTextFill(Color.BROWN);
        
        playerStats.getChildren().addAll(statsTitle, healthStat, attackStat, goldStat, woodStat);
        
        Button backBtn = new Button("BACK TO COMMAND CENTER");
        backBtn.setOnAction(e -> showCommandCenter());
        
        shopMenu.getChildren().addAll(title, upgrades, playerStats, backBtn);
        
        StackPane overlay = new StackPane();
        overlay.getChildren().addAll(root, shopMenu);
        
        Scene scene = new Scene(overlay, 1200, 800);
        stage.setScene(scene);
    }
    
    private void updateShop(VBox shopMenu) {
        showShop();
    }
    
    private void updateResourceDisplay(HBox resourceBox) {
        resourceBox.getChildren().clear();
        
        Player player = game.getPlayer();
        
        Label goldLabel = new Label("Gold: " + player.getGold());
        goldLabel.setTextFill(Color.GOLD);
        goldLabel.setStyle("-fx-font-weight: bold;");
        
        Label woodLabel = new Label("Wood: " + player.getWood());
        woodLabel.setTextFill(Color.BROWN);
        
        Label stoneLabel = new Label("Stone: " + player.getStone());
        stoneLabel.setTextFill(Color.LIGHTGRAY);
        
        Label healthLabel = new Label("HP: " + player.getHealth() + "/" + player.getMaxHealth());
        healthLabel.setTextFill(player.getHealth() > player.getMaxHealth()/2 ? Color.GREEN : Color.RED);
        
        resourceBox.getChildren().addAll(goldLabel, woodLabel, stoneLabel, healthLabel);
    }
}