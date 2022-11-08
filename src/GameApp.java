import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


interface Updatable{
    void update();
}

class GameObject extends Group implements Updatable{
    protected Translate myTranslation;
    protected Rotate myRotation;
    protected Scale myScale;

    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGTH = 800;

    public GameObject(){
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation,myRotation,myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);
        myRotation.setPivotX(0);
        myRotation.setPivotY(0);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty) {
        myTranslation.setX(tx);
        myTranslation.setY(ty);
    }

    public double getMyRotation(){
        return myRotation.getAngle();
    }

    public void update(){
        for(Node n : getChildren()){
            if(n instanceof Updatable)
                ((Updatable)n).update();
        }
    }

    void add(Node node) {
        this.getChildren().add(node);
    }
}

class Pond extends GameObject {

}

class Cloud extends GameObject {

}

class Helipad extends GameObject {
    public Helipad() {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(25);
        rectangle.setHeight(25);
        rectangle.setTranslateX(200);
        rectangle.setTranslateY(-625);
        add(Helipad);
    }
}

class Body extends GameObject{
    public Body(){
        super();
        Ellipse body = new Ellipse();
        body.setRadiusX(10);
        body.setRadiusY(10);
        body.setFill(Color.MAGENTA);
        add(body);
    }
}


class Rotors extends GameObject {
    public Rotors() {
        Polygon polygon = new Polygon();
        polygon.setFill(Color.BLUE);
        polygon.getPoints().addAll(new Double[]{
                0.0, 20.0,
                -20.0, -20.0,
                20.0, -20.0});
        add(polygon);

        //GameText text = new GameText();
        //text.setTranslateY(100);
        //text.setTranslateX(-350);
        //text.setScaleX(4);
        //add(text);

        this.getTransforms().clear();
        this.getTransforms().addAll(myRotation,myTranslation,myScale);
    }
    int offset = 1;
    public void update(){
        myTranslation.setY(myTranslation.getY() + offset);
        if(myTranslation.getY() > 40)
            offset = -offset;
        if(myTranslation.getY()<10)
            offset = -offset;
    }
}

class GameText extends GameObject {
    Text text;

    public GameText(String textString){
        text = new Text(textString);
        text.setScaleY(-1);
        text.setFont(Font.font(25));
        add(text);
    }
    public GameText(){
        this("");
    }
    public void setText(String textString){
        text.setText(textString);
    }
}

class HelicopterBody extends GameObject {
    public HelicopterBody() {
        Body myBody = new Body();
        myBody.setScaleX(1);
        myBody.setScaleY(1);

        add(makeRotors(0,40,0.25,1,0));
        add(makeRotors(0,40,0.25,1,-90));
        add(makeRotors(0,40,0.25,1,180));
        add(makeRotors(0,40,0.25,1,90));
        add(myBody);
    }
    private Rotors makeRotors(double tx, double ty, double sx, double sy,
     int degrees){
        Rotors Rotors = new Rotors();
        Rotors.rotate(degrees);
        Rotors.translate(tx*0.2, ty*0.2);
        Rotors.scale(sx, sy);
        return Rotors;
    }

    public void left() {

    }

    public void right() {
    }
}

public class GameApp extends Application {
    int counter=0;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();

        init(root);
        init(root);

        root.setScaleY(-1);
        root.setTranslateX(250);
        root.setTranslateY(-250);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RainMaker");

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(counter++ %2==0) {
                    hb.rotate(hb.getMyRotation() + 1);
                    hb.update();
                }
            }
        };
        scene.setOnKeyPressed(e ->{
            switch(e.getCode()){
                case    LEFT: hb.left();   break;
                case    RIGHT:hb.right();  break;
                default:
                    ;
            }
        });
        primaryStage.show();
        timer.start();

    }

    Helipad hp;
    HelicopterBody hb;

    public void init(Pane parent){
        parent.getChildren().clear();
        hb = new HelicopterBody();

        //Line xAxis = new Line(-125,0,125,0);

        //GameText t = new GameText("More Text!");
        //t.translate(25,125);
        //parent.getChildren().add(t);

        //Circle c = new Circle(2,Color.RED);
        //c.setTranslateX(25);
        //c.setTranslateY(125);

        //parent.getChildren().add(c);
        //parent.getChildren().add(xAxis);
        parent.getChildren().add(hb);
        parent.getChildren().add(hp);
    }
}