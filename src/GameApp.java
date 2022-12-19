import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.Random;

//Setup, show scene, and set up keyboard events
public class GameApp extends Application {
    final static int GAME_HEIGHT = 800;
    final static int GAME_WIDTH = 800;
    static final Point2D gameSize = new Point2D(GAME_HEIGHT, GAME_WIDTH);
    public void start(Stage primaryStage) {
        Game root = new Game();

        root.setScaleY(-1);
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
                root.cloudPercentSeed();
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
    PondList pond;
    CloudList cloud;
    BackgroundImage backgroundImage;
    int counter = 0;
    public Game() {
        init();
    }

    // AnimationTimer to update the GameObjects on the screen
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long nano) {
            if(counter ++ %2 == 0) {
                helicopter.update();

                // Updates the clouds on the screen
                cloud.update();
                for(int i = 0; i < cloud.sizeOfCloudList(); i++) {
                    cloud.getCloud(i).update();
                }

                // Updates the ponds on the screen
                for(int i = 0; i < pond.sizeOfPondList(); i++) {
                    pond.getPond(i).update();
                }

                // Updates the pond's capacity
                for(int i = 0; i < cloud.sizeOfCloudList(); i++) {
                    for(int j = 0; j < pond.sizeOfPondList(); j++) {
                        if(cloud.getCloud(i).pondFilling()) {
                            pond.getPond(j).pondFill();
                        }
                    }
                }
                winCondition();
            }
        }
    };

    // Initializes all the game objects
    public void init() {
        this.getChildren().clear();
        helipad = new Helipad();
        helicopter = new Helicopter();
        pond = new PondList();
        cloud = new CloudList();
        backgroundImage = new BackgroundImage();

        this.getChildren().add(backgroundImage);
        this.getChildren().add(helipad);
        this.getChildren().add(pond);
        this.getChildren().add(cloud);
        this.getChildren().add(helicopter);

        timer.start();
    }

    // Moves helicopter forward
    public void forward() {
        helicopter.forward();
    }

    // Moves helicopter backwards
    public void back() {
        helicopter.back();
    }

    // Moves helicopter left
    public void left() {
        helicopter.left();
    }

    // Moves helicopter right
    public void right() {
        helicopter.right();
    }

    // Toggles ignition on and off
    public void ignition() {
        helicopter.ignition();
    }

    // Seeds clouds on intersection
    public void cloudPercentSeed() {
        for(int i = 0; i < cloud.sizeOfCloudList(); i++) {
            if((helicopter.state instanceof HelicopterReady) && (!Shape.intersect(cloud.getCloud(i).getBounds(), helicopter.getBounds()).getBoundsInLocal().isEmpty())) {
                cloud.getCloud(i).seedCloud();
            }
        }
    }

    // Win/loss condition
    public void winCondition() {
        // Ponds are not full
        boolean PondsAreFull = false;

        // Checks each pond to see if they are full and if the helicopter has
        // the ignition turned off on the helipad
        for(int i = 0; i < pond.sizeOfPondList(); i++) {
            if (pond.getPond(i).capacity == 100 && !Shape.intersect(helicopter.getBounds(),
                    helipad.getBounds()).getBoundsInLocal().isEmpty() && helicopter.state instanceof HelicopterOff) {
                PondsAreFull = true;
            }
        }
        StringBuilder message = new StringBuilder();
        // If win conditions are met, output winning statement, and ask to
        // play again
        if(PondsAreFull) {
            timer.stop();
            message.append("You won, play again?");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    message.toString(), ButtonType.YES, ButtonType.NO);

            alert.setOnHidden(evt -> {
                if (alert.getResult() == ButtonType.YES) {
                    reset();
                } else {
                    Platform.exit();
                }
            });
            alert.show();
        }
        // If loss conditions are met, output winning statement, and ask to
        // play again
        else if(helicopter.emptyFuel()) {
            timer.stop();
            message.append("You lost, play again?");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    message.toString(), ButtonType.YES, ButtonType.NO);

            alert.setOnHidden(evt -> {
                if (alert.getResult() == ButtonType.YES) {
                    reset();
                } else {
                    Platform.exit();
                }
            });
            alert.show();
        }
    }

    // Reset all game objects
    public void reset() {
        pond.reset();
        cloud.reset();
        helicopter.reset();

        timer.stop();

        init();
    }

}

// Game objects
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

// Background image
class BackgroundImage extends GameObject {
    public BackgroundImage() {
        Image background = new Image("background.jpg");
        ImageView backgroundIM = new ImageView(background);
        backgroundIM.setTranslateX(-220);
        backgroundIM.setFitHeight(800);
        backgroundIM.setPreserveRatio(true);
        this.getChildren().add(backgroundIM);
    }
}

// Game text for percentages and fuel amount
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

// Pond list
class PondList extends GameObject {
    LinkedList<Pond> pondList;
    Random rand = new Random();

    public PondList() {
        pondList = new LinkedList<>();
        // Randomizes a number of ponds between 2 and 5
        for(int i = 0; i < rand.nextInt(2, 5); i++) {
            Pond pond = new Pond();
            pondList.add(pond);
        }
        this.getChildren().addAll(pondList);
    }

    public Pond getPond(int x) {
        return pondList.get(x);
    }

    // Returns the size of the list of ponds
    public int sizeOfPondList() {
        return pondList.size();
    }

    // Resets list of ponds
    public void reset() {
        for(Pond pond: this.pondList) {
            pond.reset();
        }
    }

    public void update() {

    }
}

// Pond class
class Pond extends GameObject {
    private Random rand = new Random();
    int capacity = rand.nextInt(30);
    int radius = capacity;
    Circle pond = new Circle(radius);
    GameText pondPercent = new GameText(capacity + "%");
    public Pond() {
        pond.setFill(Color.BLUE);
        pondPercent.setTranslateX(-5);
        pondPercent.setTranslateY(5);
        pondPercent.setColor(Color.WHITE);

        this.translate(rand.nextDouble(GameApp.gameSize.getX() / 3),
                rand.nextDouble(GameApp.gameSize.getY()));
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(pond);
        add(pondPercent);
    }

    // Fills pond up as long as it's not full
    public void pondFill() {
        if(capacity < 100) {
            pondPercent.setText(capacity++ + "%");
            System.out.println(capacity);
            radius++;
        }
    }

    // Resets pond
    public void reset() {
        capacity = 0;
        radius = 10;
    }

    // Updates the pond area based on the value of capacity
    public void update() {
        pondPercent.setText(capacity + "%");
        pond.setRadius(radius);
    }
}

// Cloud list
class CloudList extends GameObject {
    LinkedList<Cloud> cloudList;
    int cloudRandom = 0;
    Random rand = new Random();

    public CloudList() {
        cloudList = new LinkedList<>();
        for(int i = 0; i < rand.nextInt(2, 5); i++) {
            Cloud cloud = new Cloud();
            cloudList.add(cloud);
        }
        this.getChildren().addAll(cloudList);
    }

    public Cloud getCloud(int x) {
        return cloudList.get(x);
    }

    // Returns the size of the list of clouds
    public int sizeOfCloudList() {
        return cloudList.size();
    }

    // Resets the list of clouds
    public void reset() {
        for(Cloud cloud: this.cloudList) {
            cloud.reset();
        }
    }

    // Updates the states of clouds
    public void update() {
        // Loops through each cloud
        for(int i = 0; i < sizeOfCloudList(); i++) {
            // If clouds are dead, 1 to 2 respawn
            if(cloudList.get(i).cloudStateDead()) {
                cloudRandom = rand.nextInt(2);
                // If 1 respawns, add it to the cloud list
                if(cloudRandom == 1) {
                    Cloud cloud = new Cloud();
                    cloudList.add(cloud);
                    getChildren().add(cloud);
                }
                // If 1 or 2 respawns, add it to the cloud list
                if(cloudRandom == 1 || cloudRandom == 2) {
                    Cloud cloud = new Cloud();
                    cloudList.add(cloud);
                    getChildren().add(cloud);
                }
                // If 3 or 4 respawns, add it to the cloud list
                if(cloudRandom == 3 || cloudRandom == 4) {
                    cloudRandom = rand.nextInt(3);
                    if(cloudRandom == 2) {
                        Cloud cloud = new Cloud();
                        cloudList.add(cloud);
                        getChildren().add(cloud);
                    }
                }
                // Removes dead clouds from the list
                getChildren().remove(cloudList.get(i));
                cloudList.remove(i);
                i--;
            }
        }
    }
}

// Cloud states
abstract class CloudStates {
    Cloud cloud;
    public CloudStates(Cloud cloud) {
        this.cloud = cloud;
    }

    abstract void whichState(double x);
    abstract boolean cloudIsDead();
}

// Handles what happens when cloud is alive
class CloudIsAlive extends CloudStates {
    public CloudIsAlive(Cloud cloud) {
        super(cloud);
    }

    @Override
    void whichState(double cloudPositionX) {
        if(cloudPositionX > 0) {
            cloud.changeState(new CloudOnScreen(cloud));
        }
    }

    @Override
    boolean cloudIsDead() {
        return false;
    }
}

// Handles what happens while the cloud is on the screen
class CloudOnScreen extends CloudStates {
    public CloudOnScreen(Cloud cloud) {
        super(cloud);
    }

    @Override
    void whichState(double cloudPositionX) {
        if(cloudPositionX > GameApp.gameSize.getX()) {
            cloud.changeState(new CloudIsDead(cloud));
        }
    }

    @Override
    boolean cloudIsDead() {
        return false;
    }
}

// Handles what happens when the cloud goes off-screen (dies)
class CloudIsDead extends CloudStates {
    CloudIsDead(Cloud cloud) {
        super(cloud);
    }

    @Override
    void whichState(double x) {

    }

    @Override
    boolean cloudIsDead() {
        return true;
    }
}

// Cloud class
class Cloud extends GameObject {
    private Random rand = new Random();
    int saturation = 0;
    int delayCounter = 0;
    int r = 255;
    int g = 255;
    int b = 255;
    int cloudPositionX = rand.nextInt(200);
    double cloudSpeed = 0.1;
    Circle cloud = new Circle(50);
    Rectangle boundingBox;
    GameText cloudPercent = new GameText(saturation + "%");
    Boolean cloudIsThirty = false;
    CloudStates state;
    double getCloudPositionY = rand.nextDouble(GameApp.gameSize.getY() / 3,
     GameApp.gameSize.getY());
    public Cloud() {
        state = new CloudIsAlive(this);
        cloud.setFill(Color.rgb(r, g, b));
        cloudPercent.setTranslateX(-15);
        cloudPercent.setTranslateY(5);
        cloudPercent.setColor(Color.BLUE);

        // Cloud bounding box
        boundingBox = new Rectangle();
        boundingBox.setTranslateX(-50);
        boundingBox.setTranslateY(-50);
        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setStroke((Color.TRANSPARENT));
        boundingBox.setFill(Color.TRANSPARENT);
        add(boundingBox);

        // Spawns clouds on left side of the screen
        this.translate(150, getCloudPositionY);
        this.translate(rand.nextDouble(GameApp.gameSize.getX() / 3),
         rand.nextDouble(GameApp.gameSize.getY()));
        this.getTransforms().clear();
        this.getTransforms().add(translation);

        add(cloud);
        add(cloudPercent);
    }

    // Returns bounds of cloud
    public Shape getBounds() {
        return boundingBox;
    }

    // Pond fills only when the cloud is above 30%
    public boolean pondFilling() {
        return cloudIsThirty;
    }

    // Seeding the clouds and turns the clouds grey
    public void seedCloud() {
        if(saturation < 100) {
            cloudPercent.setText(saturation++ + "%");
            cloud.setFill(Color.rgb(r--, g--, b--));
        }
    }

    // Resets the clouds
    public void reset() {
        saturation = 0;
        delayCounter = 0;
        r = 255;
        g = 255;
        b = 255;
        cloudIsThirty = false;
        cloudPositionX = -200;
        cloud.setFill(Color.rgb(r, g, b));
    }

    // Checks to see if the cloud is off the screen (dead)
    public boolean cloudStateDead() {
        return state.cloudIsDead();
    }

    // Changes the states of the clouds
    public void changeState(CloudStates state) {
        this.state = state;
    }

    // Has a counter to control speed that the cloud seeds, as long as the
    // saturation in not 0, continue to seed
    public void update() {
        if(delayCounter == 0) {
            delayCounter = 50;
            if (saturation != 0) {
                cloudPercent.setText(saturation-- + "%");
                cloud.setFill(Color.rgb(r++, g++, b++));
                if(saturation >= 30) {
                    cloudIsThirty = true;
                }
            }
        }
        // Decrement counter if the clouds are not being seeded
        else if(delayCounter < 51) {
            delayCounter--;
            cloudIsThirty = false;
        }
        // Movement of left to right of the clouds
        translate(translation.getX() + cloudSpeed, translation.getY());
        state.whichState(translation.getX());
        getTransforms().clear();
        getTransforms().add(translation);

    }

}

// Helipad class
class Helipad extends GameObject {
        Rectangle helipadOut;
    public Helipad() {
        helipadOut = new Rectangle(80, 80);
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

    // Get outer bounds of the helipad
    public Shape getBounds() {
        return helipadOut;
    }
}

// HeliBody class
class HeliBody extends GameObject {
    public HeliBody() {
        // Imported a helicopter body image
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

// HeliBlade class
class HeliBlade extends GameObject {
    int bladeSpeed = 0;

    public HeliBlade() {
        // Draw and transform the helicopter blades
        Line blade = new Line();

        blade.setStrokeWidth(5);
        blade.setStartX(-25);
        blade.setEndX(25);
        blade.setStartY(-25);
        blade.setEndY(25);
        blade.setFill(Color.BLACK);
        this.getChildren().add(blade);

        // AnimationTimer for the helicopter blades to spin
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long nano) {
                HeliBlade.this.setRotate(HeliBlade.this.getRotate() + bladeSpeed);
            }
        };
        timer.start();
    }

    // Updates the speed on the helicopter blades
    public void update(int bladeSpeed) {
        this.bladeSpeed = bladeSpeed;
    }
}

abstract class HelicopterStates {
    Helicopter helicopter;
    int maxSpeed = 20;

    public HelicopterStates(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    abstract void Ignition();
    abstract int BladeState(int bladeSpeed);
}

class HelicopterOff extends HelicopterStates {
    HelicopterOff(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new HelicopterStarting(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {
        return 0;
    }
}

class HelicopterStarting extends HelicopterStates {

    HelicopterStarting(Helicopter helicopter) {
        super(helicopter);
    }
    @Override
    void Ignition() {
        helicopter.changeState(new HelicopterStopping(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {
        if(bladeSpeed < maxSpeed) {
            bladeSpeed++;
        }
        if(bladeSpeed == maxSpeed) {
            helicopter.changeState(new HelicopterReady(helicopter));
        }
        return bladeSpeed;
    }
}

class HelicopterStopping extends HelicopterStates {
    HelicopterStopping(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new HelicopterStarting(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {
        if(bladeSpeed > 0) {
            bladeSpeed--;
        }
        if(bladeSpeed == 0) {
            helicopter.changeState(new HelicopterOff(helicopter));
        }
        return bladeSpeed;
    }
}

class HelicopterReady extends HelicopterStates {
    HelicopterReady(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new HelicopterStopping(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {
        return maxSpeed;
    }
}

// Helicopter class
class Helicopter extends GameObject {
    double speedVertical;
    double maxSpeed = 10;
    double maxSpeedBack = -2;
    double rotateHeli;
    int fuel = 25000;
    int bladeSpeed;
    boolean emptyTank = false;
    Rectangle boundingBox;
    HeliBody heliBody = new HeliBody();
    HeliBlade heliBlade = new HeliBlade();
    GameText helicopterFuel = new GameText("F:" + fuel);
    HelicopterStates state;

    public Helicopter() {
        state = new HelicopterOff(this);
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

        helicopterFuel.setTranslateX(-20);
        helicopterFuel.setTranslateY(-18);
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
        if(state instanceof HelicopterReady) {
            speedVertical = speedVertical + 0.1;
        }
    }

    public void back() {
        if(state instanceof HelicopterReady) {
            speedVertical = speedVertical - 0.1;
        }
    }

    public void left() {
        if(state instanceof HelicopterReady) {
            rotateHeli = rotateHeli + 15;
        }
    }

    public void right() {
        if(state instanceof HelicopterReady) {
            rotateHeli = rotateHeli - 15;
        }
    }

    public void fuelDecrease() {
        if(fuel != 0) {
            fuel--;
        }
    }

    public void ignition() {
        state.Ignition();
    }

    public boolean emptyFuel() {
        if(fuel == 0) {
            emptyTank = true;
        }
        return emptyTank;
    }

    public void changeState(HelicopterStates state) {
        this.state = state;
    }

    public void reset() {
        speedVertical = 0;
        rotateHeli = 0;
        maxSpeed = 0;
        maxSpeedBack = 0;
        fuel = 0;
        bladeSpeed = 0;
        state = new HelicopterOff(this);
    }

    public void update() {
        this.rotate(rotateHeli);
        this.getTransforms().clear();
        translation.setX(translation.getX() + Math.sin(Math.toRadians(rotateHeli)) * -speedVertical);
        translation.setY(translation.getY() + Math.cos(Math.toRadians(rotateHeli)) * speedVertical);
        this.getTransforms().addAll(translation, rotate);

        bladeSpeed = state.BladeState(bladeSpeed);
        heliBlade.update(bladeSpeed);

        if(state instanceof HelicopterReady) {
            this.fuelDecrease();
            helicopterFuel.setText("F" + fuel);
        }
    }
}

interface Updateable {
    void update();
}



