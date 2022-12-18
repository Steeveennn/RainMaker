import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import java.util.Random;

public class GameApp extends Application {
    static final Point2D gameSize = new Point2D(800, 800);
    public void start(Stage primaryStage) {
        Game root = new Game();

        root.setScaleY(-1);
        //root.setTranslateX(250);
        //root.setTranslateY(-70);
        Scene scene = new Scene(root, gameSize.getX(), gameSize.getY());
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RainMaker");

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP) {
                root.forward();
            }
            if(e.getCode() == KeyCode.DOWN) {
                root.back();
            }
            if(e.getCode() == KeyCode.LEFT) {
                root.left();
            }
            if(e.getCode() == KeyCode.RIGHT) {
                root.right();
            }
            if(e.getCode() == KeyCode.I) {
                root.ignition();
            }
            if(e.getCode() == KeyCode.SPACE) {
                root.seedCloud();
            }
            if(e.getCode() == KeyCode.R) {
                root.reset();
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
    Cloud cloud;
    BackgroundImage backgroundImage;
    int counter = 0;
    public Game() {
        init();
    }

    public void init() {
        this.getChildren().clear();
        helipad = new Helipad();
        helicopter = new Helicopter();
        pond = new Pond();
        cloud = new Cloud();
        backgroundImage = new BackgroundImage();

        this.getChildren().add(backgroundImage);
        this.getChildren().add(helipad);
        this.getChildren().add(pond);
        this.getChildren().add(cloud);
        this.getChildren().add(helicopter);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long nano) {
                if(counter ++ %2 == 0) {
                    helicopter.update();
                    cloud.update();
                    pond.update();
                    if(cloud.pondFilling()) {
                        pond.pondFill();
                    }
                }
            }
        };
        timer.start();
    }

    public void forward() {
        helicopter.forward();
    }

    public void back() {
        helicopter.back();
    }

    public void left() {
        helicopter.left();
    }

    public void right() {
        helicopter.right();
    }

    public void ignition() {
        helicopter.ignition();
    }

    public void seedCloud() {
        if(!Shape.intersect(cloud.getBounds(), helicopter.getBounds()).getBoundsInLocal().isEmpty()) {
            cloud.seedCloud();
        }
    }

    public void reset() {
        init();
    }

}

class GameObject extends Group implements Updateable {
    Scale scale;
    Translate translation;
    Rotate rotate;

    public GameObject() {
        scale = new Scale();
        translation = new Translate();
        rotate = new Rotate();
    }

    public void rotate(double degrees) {
        rotate.setAngle(degrees);
        rotate.setPivotX(0);
        rotate.setPivotY(0);
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

class BackgroundImage extends GameObject {
    public BackgroundImage() {
        Image background = new Image("background.jpg");
        ImageView backgroundIM = new ImageView(background);
        backgroundIM.setTranslateX(-300);
        backgroundIM.setTranslateY(-70);
        backgroundIM.setFitHeight(800);
        backgroundIM.setPreserveRatio(true);
        this.getChildren().add(backgroundIM);
    }
}

class GameText extends GameObject{

    Text Text;

    public void setColor(Color color){
        Text.setFill(color);
    }

    public GameText(String textString){
        Text = new Text(textString);
        Text.setScaleY(-1);
        add(Text);
    }
    public GameText(){
        this("");
    }
    public void setText(String textString){
        Text.setText(textString);
    }

}

class Pond extends GameObject {
    private Random rand = new Random();
    int radius = 10;
    int capacity = 0;
    Circle pond = new Circle(radius);
    GameText pondPercent = new GameText(capacity + "%");
    public Pond() {
        pond.setFill(Color.BLUE);
        pondPercent.setTranslateX(-5);
        pondPercent.setTranslateY(5);
        pondPercent.setColor(Color.WHITE);

        this.translate(-100, 400);
        this.translate(rand.nextInt(580), rand.nextInt(580));
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(pond);
        add(pondPercent);
    }

    public void pondFill() {
        if(capacity <= 100) {
            pondPercent.setText(capacity++ + "%");
            radius++;
        }
    }

    public void update() {
        pondPercent.setText(capacity + "%");
        pond.setRadius(radius);
    }
}

class Cloud extends GameObject {
    private Random rand = new Random();
    Circle cloud = new Circle(50);
    Rectangle boundingBox;
    int saturation = 0;
    int delayCounter = 0;
    GameText cloudPercent = new GameText(saturation + "%");
    Boolean cloudIsThirty = false;
    public Cloud() {
        cloud.setFill(Color.WHITE);
        cloudPercent.setTranslateX(-15);
        cloudPercent.setTranslateY(5);
        cloudPercent.setColor(Color.BLUE);

        boundingBox = new Rectangle();
        boundingBox.setTranslateX(-50);
        boundingBox.setTranslateY(-50);
        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setStroke((Color.YELLOW));
        boundingBox.setFill(Color.TRANSPARENT);
        add(boundingBox);

        this.translate(150, 200);
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(cloud);
        add(cloudPercent);
    }

    public Shape getBounds() {
        return boundingBox;
    }

    public boolean pondFilling() {
        return cloudIsThirty;
    }

    public void seedCloud() {
        if(saturation <= 100) {
            cloudPercent.setText(saturation++ + "%");
        }
    }

    public void update() {
        if(delayCounter == 0) {
            delayCounter = 50;
            if (saturation != 0) {
                cloudPercent.setText(saturation-- + "%");
                if(saturation >= 30) {
                    cloudIsThirty = true;
                }
            }
        }
        else if(delayCounter < 51) {
            delayCounter--;
            cloudIsThirty = false;
        }
    }

}

class Helipad extends GameObject {
    public Helipad() {
        Rectangle helipadOut = new Rectangle(80, 80);
        helipadOut.setStroke((Color.GRAY));

        Circle helipadIn = new Circle(30);
        helipadIn.setCenterX(helipadOut.getWidth()/2);
        helipadIn.setCenterY(helipadOut.getHeight()/2);
        helipadIn.setStroke(Color.GRAY);

        this.translate((GameApp.gameSize.getX() - 80) /2,
        GameApp.gameSize.getY() / 6);
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(helipadOut);
        add(helipadIn);
    }
}

class HeliBody extends GameObject {
    public HeliBody() {
        Image heliBody = new Image("helicopter.png");
        ImageView heliIM = new ImageView(heliBody);
        this.getChildren().add(heliIM);

        heliIM.setTranslateX(-111);
        heliIM.setTranslateY(-230);
        heliIM.setScaleX(0.2);
        heliIM.setScaleY(-0.2);
        //scale(2, 2);

    }
}

class HeliBlade extends GameObject {
    int bladeSpeed = 0;

    public HeliBlade() {
        Line blade = new Line();

        blade.setStrokeWidth(5);
        blade.setStartX(-25);
        blade.setEndX(25);
        blade.setStartY(-25);
        blade.setEndY(25);
        blade.setFill(Color.BLACK);
        this.getChildren().add(blade);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long nano) {
                HeliBlade.this.setRotate(HeliBlade.this.getRotate() + bladeSpeed);
            }
        };
        timer.start();
    }

    public void update(int bladeSpeed) {
        this.bladeSpeed = bladeSpeed;
    }
}

class Helicopter extends GameObject {
    double speedVertical;
    double maxSpeed = 10;
    double maxSpeedBack = -2;
    double rotateHeli;
    int fuel = 25000;
    boolean ignition = false;
    Rectangle boundingBox;
    HeliBody heliBody = new HeliBody();
    HeliBlade heliBlade = new HeliBlade();
    GameText helicopterFuel = new GameText("F:" + fuel);
    public Helicopter() {
        Ellipse helicopter = new Ellipse();
        helicopter.setRadiusX(10);
        helicopter.setRadiusY(10);
        helicopter.setFill(Color.YELLOW);

        this.translate(GameApp.gameSize.getX() / 2,
          GameApp.gameSize.getY()/ 4.5);
         this.getTransforms().clear();
         this.getTransforms().add(translation);

        add(helicopter);
        add(heliBody);
        add(heliBlade);

        helicopterFuel.setTranslateX(-25);
        helicopterFuel.setTranslateY(-15);
        helicopterFuel.setColor(Color.YELLOW);
        add(helicopterFuel);

        boundingBox = new Rectangle();
        boundingBox.setTranslateX(-30);
        boundingBox.setTranslateY(-30);
        boundingBox.setWidth(60);
        boundingBox.setHeight(60);
        boundingBox.setStroke((Color.YELLOW));
        boundingBox.setFill(Color.TRANSPARENT);
        add(boundingBox);

        Line line = new Line(0, 10, 0, 30);
        line.setStroke(Color.YELLOW);
        add(line);
    }

    public Shape getBounds() {
        return boundingBox;
    }

    public void forward() {
        if(ignition && speedVertical < maxSpeed) {
            speedVertical = speedVertical + 0.1;
        }
    }

    public void back() {
        if(ignition && speedVertical > maxSpeedBack) {
            speedVertical = speedVertical - 0.1;
        }
    }

    public void left() {
        if(ignition) {
            rotateHeli = rotateHeli + 15;
        }
    }

    public void right() {
        if(ignition) {
            rotateHeli = rotateHeli - 15;
        }
    }

    public void fuelDecrease() {
        if(fuel != 0) {
            fuel--;
        }
    }

    public void ignition() {
        ignition =! ignition;
    }

    public void update() {
        this.rotate(rotateHeli);
        this.getTransforms().clear();
        translation.setX(translation.getX() + Math.sin(Math.toRadians(rotateHeli)) * -speedVertical);
        translation.setY(translation.getY() + Math.cos(Math.toRadians(rotateHeli)) * speedVertical);
        this.getTransforms().addAll(translation, rotate);

        this.fuelDecrease();
        helicopterFuel.setText("F" + fuel);
    }
}

interface Updateable {
    void update();
}



