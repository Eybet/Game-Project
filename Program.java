import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Game game = new Game();
        GameUI ui = new GameUI(game, primaryStage);
        ui.showMainMenu();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}