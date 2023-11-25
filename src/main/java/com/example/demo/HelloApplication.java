package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    //start method for javaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Find shortest path");
        Map map = new Map();
        AlgorithmLi algorithmLi = new AlgorithmLi(map);
        map.mapChis();

        Canvas centralCanvas = createCanvas(map);
        Canvas leftCanvas = createCanvas(map);
        Canvas rightCanvas = createCanvas(map);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            map.reset();
            algorithmLi.reset();
            map.drawMap(leftCanvas.getGraphicsContext2D());
            map.drawMap(centralCanvas.getGraphicsContext2D());
            map.drawMap(rightCanvas.getGraphicsContext2D());
        });

        HBox root = new HBox(map.getCellSize());
        root.getChildren().addAll(leftCanvas, centralCanvas, rightCanvas);
        root.getChildren().add(resetButton);

        leftCanvas.setOnMouseClicked(event -> handleCanvasClick(leftCanvas, event, leftCanvas, centralCanvas, rightCanvas,map, algorithmLi));
//        centralCanvas.setOnMouseClicked(event -> handleCanvasClick(centralCanvas, event, leftCanvas, centralCanvas, rightCanvas));
//        rightCanvas.setOnMouseClicked(event -> handleCanvasClick(rightCanvas, event, leftCanvas, centralCanvas, rightCanvas));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Canvas createCanvas(Map map) {
        Canvas canvas = new Canvas(map.getWIDTH() * map.getCellSize(), map.getHEIGHT() * map.getCellSize());
        map.drawMap(canvas.getGraphicsContext2D());
        return canvas;
    }

    private void handleCanvasClick(Canvas canvas, javafx.scene.input.MouseEvent event, Canvas leftCanvas, Canvas centralCanvas, Canvas rightCanvas, Map mapa, AlgorithmLi algorithmLi) {
        int x = (int) (event.getX() / mapa.getCellSize());
        int y = (int) (event.getY() / mapa.getCellSize());

        int map[][] = mapa.getMap();
        if (mapa.getStartX() == -1) {
            if (map[x][y] != 0) {
                mapa.setStartX(x);
                mapa.setStartY(y);
                mapa.drawMap(leftCanvas.getGraphicsContext2D());
                mapa.drawMap(rightCanvas.getGraphicsContext2D());
                mapa.drawMap(centralCanvas.getGraphicsContext2D());
            }
        } else if (mapa.getEndX() == -1) {
            if (map[x][y] != 0) {
                mapa.setEndX(x);
                mapa.setEndY(y);
                mapa.drawMap(leftCanvas.getGraphicsContext2D(), algorithmLi.getVisited(), algorithmLi.getPath());
//                drawMap(rightCanvas.getGraphicsContext2D());
//                drawMap(centralCanvas.getGraphicsContext2D());
                int min_dist = -1;

                if (canvas.equals(leftCanvas)) {
                    min_dist = algorithmLi.findShortestPathLi(map, mapa.getStartX(), mapa.getStartY(), mapa.getEndX(), mapa.getEndY(),leftCanvas.getGraphicsContext2D());
                }
//                else if (canvas.equals(rightCanvas)) {
//                    min_dist = findShortestPathLi(map, startX, startY, endX, endY);
//                } else {
//                    min_dist = findShortestPathLi(map, startX, startY, endX, endY);
//                }

//                drawMap(canvas.getGraphicsContext2D());

                if (min_dist != -1) {
                    int stepDelayMillis = 50;
                    algorithmLi.displayPathStepByStep(leftCanvas.getGraphicsContext2D(), stepDelayMillis);
                } else {
                    System.err.println("Destination cannot be reached from source");
                }
            }
        }
    }


    //main method
    public static void main(String[] args) {
        launch(args);
    }
}