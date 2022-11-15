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
import org.w3c.dom.css.Rect;

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
class Game extends Pane {
    Helipad helipad;
    Helicopter helicopter;
    Pond pond;
    public Game() {
        this.getChildren().clear();
        helipad = new Helipad();
        helicopter = new Helicopter();
        pond = new Pond();

        this.getChildren().add(helipad);
        this.getChildren().add(helicopter);
        this.getChildren().add(pond);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long nano) {

            }
        };
        timer.start();
    }
}

class GameObject extends Group implements Updateable {
    Scale scale;
    Translate translation;
    Rotate rotate;

    public GameObject() {
        scale = new Scale();
        translation = new Translate();
    }

    public void scale(double sx, double sy) {
        scale.setX(sx);
        scale.setY(sy);
    }

    public void translate(double tx, double ty) {
        translation.setX(tx);
        translation.setY(ty);
    }

    void add(Node node) {
        this.getChildren().add(node);
    }

    public void update() {
        for(Node n : getChildren()) {
            if(n instanceof Updateable) {
                ((Updateable)n).update();
            }
        }
    }
}

class Pond extends GameObject {
    public Pond() {
        Circle pond = new Circle(10);
        pond.setFill(Color.BLUE);

        Text pondPercent = new Text("0%");
        pondPercent.setScaleY(-1);
        pondPercent.setTranslateX(-5);
        pondPercent.setTranslateY(5);
        pondPercent.setFill(Color.WHITE);
        pondPercent.setFont(Font.font(15));

        this.translate(100, 100);
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(pond);
        add(pondPercent);
    }
}

class Cloud extends GameObject {

}

class Helipad extends GameObject {
    public Helipad() {
        Rectangle helipadOut = new Rectangle(80, 80);
        helipadOut.setStroke((Color.GRAY));

        Circle helipadIn = new Circle(30);
        helipadIn.setCenterX(helipadOut.getWidth()/2);
        helipadIn.setCenterY(helipadOut.getHeight()/2);
        helipadIn.setStroke(Color.GRAY);

        this.translate(-40,-40);
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(helipadOut);
        add(helipadIn);
    }
}

class Helicopter extends GameObject {
    public Helicopter() {
        Ellipse helicopter = new Ellipse();
        helicopter.setRadiusX(10);
        helicopter.setRadiusY(10);
        helicopter.setFill(Color.YELLOW);
        add(helicopter);

        Line line = new Line(0, 10, 0, 30);
        line.setStroke(Color.YELLOW);
        add(line);
    }
}

interface Updateable {
    void update();
}



