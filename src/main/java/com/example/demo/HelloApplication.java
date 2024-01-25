package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    //start method for javaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Find shortest path");
        Mapaa map = new Mapaa();
        map.mapChis();
        AlgorithmLi algorithmLi = new AlgorithmLi(map);
        AlgorithmDijkstra algorithmDijkstra = new AlgorithmDijkstra(map);
        AStar aStar = new AStar(map);

        Canvas centralCanvas = createCanvas(map);
        Canvas leftCanvas = createCanvas(map);
        Canvas rightCanvas = createCanvas(map);
        Label labelLi = new Label("Алгоритм Ли");
        Label labelDijkstra = new Label("Алгоритм Дейкстры");
        Label labelAStar = new Label("Алгоритм А*");


        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            map.reset();
            algorithmLi.reset();
            algorithmDijkstra.reset();
            aStar.reset();
            map.drawMap(leftCanvas.getGraphicsContext2D());
            map.drawMap(centralCanvas.getGraphicsContext2D());
            map.drawMap(rightCanvas.getGraphicsContext2D());
        });

        VBox vboxLi = createCanvasWithLabel(leftCanvas, labelLi);
        VBox vboxDijkstra = createCanvasWithLabel(centralCanvas, labelDijkstra);
        VBox vboxAStar = createCanvasWithLabel(rightCanvas, labelAStar);

        // Добавляем VBox в HBox
        HBox root = new HBox(map.getCellSize());
        root.getChildren().addAll(vboxLi, vboxDijkstra, vboxAStar);
        root.getChildren().add(resetButton);
        leftCanvas.setOnMouseClicked(event -> handleCanvasClick(leftCanvas, event, leftCanvas, centralCanvas, rightCanvas,map, algorithmLi, algorithmDijkstra, aStar));
        centralCanvas.setOnMouseClicked(event -> handleCanvasClick(centralCanvas, event, leftCanvas, centralCanvas, rightCanvas, map, algorithmLi, algorithmDijkstra, aStar));
        rightCanvas.setOnMouseClicked(event -> handleCanvasClick(rightCanvas, event, leftCanvas, centralCanvas, rightCanvas, map, algorithmLi, algorithmDijkstra, aStar));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private VBox createCanvasWithLabel(Canvas canvas, Label label) {
        VBox box = new VBox();
        box.getChildren().addAll(label, canvas);
        return box;
    }
    private Canvas createCanvas(Mapaa map) {
        Canvas canvas = new Canvas(map.getWIDTH() * map.getCellSize(), map.getHEIGHT() * map.getCellSize());
        map.drawMap(canvas.getGraphicsContext2D());
        return canvas;
    }
    private void handleCanvasClick(Canvas canvas, javafx.scene.input.MouseEvent event, Canvas leftCanvas, Canvas centralCanvas, Canvas rightCanvas, Mapaa mapa, AlgorithmLi algorithmLi, AlgorithmDijkstra algorithmDijkstra, AStar aStar) {
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
                mapa.drawMap(centralCanvas.getGraphicsContext2D(), algorithmDijkstra.getVisited(), algorithmDijkstra.getPath());
                mapa.drawMap(rightCanvas.getGraphicsContext2D(), aStar.getVisited(), aStar.getPath());

                int min_distLi = -1;
                int min_distDij = -1;
                int min_distA = -1;

                min_distLi = algorithmLi.findShortestPathLi(map, mapa.getStartX(), mapa.getStartY(), mapa.getEndX(), mapa.getEndY());
                min_distDij = algorithmDijkstra.dijkstra(mapa.getStartX(), mapa.getStartY(), mapa.getEndX(), mapa.getEndY());
                min_distA = aStar.aStar(mapa.getStartX(), mapa.getStartY(), mapa.getEndX(), mapa.getEndY());
                if (min_distLi != -1) {
                    int stepDelayMillis = 50;
                    algorithmLi.displayPathStepByStep(leftCanvas.getGraphicsContext2D(), stepDelayMillis);
                } else {
                    System.err.println("Destination cannot be reached from source by algorithm Li");
                }
                if (min_distDij != -1) {
                    int stepDelayMillis = 50;
                    algorithmDijkstra.displayPathStepByStep(centralCanvas.getGraphicsContext2D(), stepDelayMillis);
                } else {
                    System.err.println("Destination cannot be reached from source by algorithm Dijkstra");
                }
                if (min_distA != -1) {
                    int stepDelayMillis = 50;
                    aStar.displayPathStepByStep(rightCanvas.getGraphicsContext2D(), stepDelayMillis);
                } else {
                    System.err.println("Destination cannot be reached from source by algorithm A*");
                }
            }
        }
    }

    //main method
    public static void main(String[] args) {
        launch(args);
    }
}