import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class GameApp extends Application {
    public void start(Stage primaryStage) {
        Game root = new Game();

        root.setScaleY(-1);
        root.setTranslateX(250);
        root.setTranslateY(-70);
        Scene scene = new Scene(root, 500, 800);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RainMaker");

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP) {

            }
            if(e.getCode() == KeyCode.DOWN) {

            }
            if(e.getCode() == KeyCode.LEFT) {

            }
            if(e.getCode() == KeyCode.RIGHT) {

            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Rules of the game and contains the game objects
public class Game extends Pane {

}

class GameObject extends Group implements Updateable {

}

class Pond extends GameObject {

}

class Cloud extends GameObject {

}

class Helipad extends GameObject {

}

class Helicopter extends GameObject {

}

interface Updateable {
    void update();
}



