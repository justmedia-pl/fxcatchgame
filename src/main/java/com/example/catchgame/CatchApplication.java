package com.example.catchgame;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CatchApplication extends Application {

    private final int fall = 500;
    private final int basketW = 60;
    private final int basketH = 55;
    private final int obstacleW = 20;
    private final int obstacleH = 20;

    private Pane pane;
    private int speed = 1;
    private int points = 0;
    private int missed = 0;

    private double mouseX;
    private double mouseY;
    Rectangle rectangle;
    Group obstaclesGroup;
    CatchController catchControllerController;
    Group group;

    private class Obstacle {
        Shape obstacle;
    }





    List<Obstacle> obstacles = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(CatchApplication.class.getResource("catch-game.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        catchControllerController = fxmlLoader.getController();
        scene.getStylesheets().add(getClass().getResource("catchapp.css").toExternalForm());
        pane = catchControllerController.pane;
        group = new Group();
        obstaclesGroup = new Group();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        rectangle = new Rectangle();
        rectangle.setHeight(basketH);
        rectangle.setWidth(basketW);
        rectangle.setX(0);
        rectangle.setY(pane.getHeight()-basketH);
        System.out.println(stage.getHeight());
        System.out.println(pane.getHeight());
        Image img = new Image("basket.png");
        rectangle.setFill(new ImagePattern(img));
        group.getChildren().add(rectangle);
        pane.getChildren().addAll(group,obstaclesGroup);
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                gameUpdate();
            }
        };
        animationTimer.start();

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(fall), event -> {
                    System.out.println("drop");
                    speed += fall / 3000;
                    obstacles.add(createObstacle());
                    System.out.println(obstacles.size());
                    obstaclesGroup.getChildren().add(obstacles.get(obstacles.size() -1).obstacle);

                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseX = mouseEvent.getX();
                mouseY =  mouseEvent.getY();
                //if mouse move is pane width - long of board move
                if (mouseX < pane.getWidth() - basketW/2 && mouseX > basketW/2) {
                    rectangle.setX(mouseX - basketW/2);
                } else if (mouseX > pane.getWidth() - basketW/2) {
                    rectangle.setX(pane.getWidth() - basketW);
                } else if (mouseX < basketW/2 ) {
                    rectangle.setX(0);
                    System.out.println("min");
                }
                catchControllerController
                        .position.setText(
                                String.format("%,.2f",mouseX)
                                        +":"
                                        +String.format("%,.2f",mouseY));

            }
        });
    }
    private boolean collisionDetection(Obstacle currentObstacle){
        double currentObstacleX = currentObstacle.obstacle.getLayoutX();
        double currentObstacleY = currentObstacle.obstacle.getLayoutY();
        //collision
        if (between(currentObstacleX, rectangle.getX(),rectangle.getX()+rectangle.getWidth())
        && between(currentObstacleY, rectangle.getY(),rectangle.getY()+rectangle.getHeight())){
            points ++;
            return true;
        }
        return false;
    }

    private boolean outOfBounds(Obstacle currentObstacle){
        double currentObstacleY = currentObstacle.obstacle.getLayoutY();
        //collision
        return currentObstacleY >= pane.getHeight();
    }

    private boolean between(double value, double min, double max){
        return value > min && value < max;
    }


    private Obstacle createObstacle(){
        Obstacle obstacle = new Obstacle();
        double random = ThreadLocalRandom.current().nextDouble(obstacleW/2, pane.getWidth()-obstacleW/2);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(obstacleW);
        rectangle.setHeight(obstacleH);

        Image img = new Image("apple.png");
        rectangle.setFill(new ImagePattern(img));
        rectangle.setLayoutX(random);
        obstacle.obstacle = rectangle;
        return obstacle;
    }

    private void gameUpdate(){
        for (int i = 0; i < obstacles.size();i++){
            Shape currentCircleUpdate = obstacles.get(i).obstacle;
            currentCircleUpdate.setLayoutY(currentCircleUpdate.getLayoutY() + speed + (currentCircleUpdate.getLayoutY()/ 150 ));
            catchControllerController.points.setText(String.valueOf(points));
           if (collisionDetection(obstacles.get(i))){
               obstaclesGroup.getChildren().remove(obstacles.get(i).obstacle);
                obstacles.remove(i);


            } else {
               if (outOfBounds(obstacles.get(i))){
                   obstaclesGroup.getChildren().remove(obstacles.get(i).obstacle);
                   missed++;
                   catchControllerController.missed.setText(String.valueOf(missed));
                   obstacles.remove(i);
               }
           }
        }

    }


    public static void main(String[] args) {
        launch();
    }
}