package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class HelloApplication extends Application {
    private static final int WIDTH = 70;
    private static final int HEIGHT = 70;

    private int startX = -1;
    private int startY = -1;
    private int endX = -1;
    private int endY = -1;

    private final int[][] map = new int[WIDTH][HEIGHT];
    private final boolean[][] path = new boolean[WIDTH][HEIGHT];
    List<Integer[]> pathList = new ArrayList<>();
    private void map(){
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (Math.random() < 0.6) {
                    map[x][y] = 1;
                } else {
                    map[x][y] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Visualization");
        map();

        Canvas canvas = new Canvas(WIDTH*10, HEIGHT*10);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawMap(gc);

        Pane root = new Pane();
        root.getChildren().add(canvas);

        canvas.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / 10);
            int y = (int) (event.getY() / 10);

            if (startX == -1) {
                if (map[x][y] == 1) {
                    startX = x;
                    startY = y;
                    drawMap(gc);
                }
            } else if (endX == -1) {
                if (map[x][y] == 1) {
                    endX = x;
                    endY = y;
                    drawMap(gc);
                    int min_dist = findShortestPathLength(map, startX, startY, endX, endY);
                    if (min_dist != -1) {
                        int stepDelayMillis = 100;
                        displayPathStepByStep(gc, stepDelayMillis);
                        System.out.println("The shortest path from source to destination has length " + min_dist);
                    } else {
                        System.out.println("Destination cannot be reached from source");
                    }
                }
            }
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    private void displayPathStepByStep(GraphicsContext gc, int stepDelayMillis) {
        if (pathList.size() > 0) {
            Integer[] pathCoords = pathList.remove(pathList.size() - 1);
            path[pathCoords[0]][pathCoords[1]] = true;
            drawMap(gc);

            PauseTransition pause = new PauseTransition(Duration.millis(stepDelayMillis));
            pause.setOnFinished(event -> displayPathStepByStep(gc, stepDelayMillis));
            pause.play();
        }
    }
    private void drawMap(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * 10, HEIGHT * 10);

        int cellSize = 10;

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == startX && y == startY) {
                    gc.setFill(javafx.scene.paint.Color.BLUE);
                } else if (x == endX && y == endY) {
                    gc.setFill(javafx.scene.paint.Color.RED);
                } else if (map[x][y] == 0) {
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                } else if (path[x][y]) {
                    gc.setFill(javafx.scene.paint.Color.YELLOW);
                } else {
                    gc.setFill(javafx.scene.paint.Color.WHITE);
                }
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }


    private static final int[] row = { -1, -1,  0,  1, 1, 1, 0, -1};
    private static final int[] col = {  0, -1, -1, -1, 0, 1, 1,  1};

    private static boolean isValid(int[][] mat, boolean[][] visited, int x, int y)
    {
        return (x >= 0) && (x < mat.length) && (y >= 0) && (y < mat[0].length)
                && mat[x][y] == 1 && !visited[x][y];
    }

    private int findShortestPathLength(int[][] mat, int i, int j, int x, int y)
    {
        if (mat == null || mat.length == 0 || mat[i][j] == 0 || mat[x][y] == 0) {
            return -1;
        }

        int M = mat.length;
        int N = mat[0].length;

        boolean[][] visited = new boolean[M][N];

        Queue<Node> q = new ArrayDeque<>();

        visited[i][j] = true;
        q.add(new Node(i, j, 0,null));

        int min_dist = Integer.MAX_VALUE;

        while (!q.isEmpty())
        {
            Node node = q.poll();

            i = node.x;
            j = node.y;
            int dist = node.dist;

            if (i == x && j == y)
            {
                min_dist = dist;
                while (node != null) {
                    pathList.add(new Integer[]{node.x,node.y});
                    node = node.prev;
                }
                break;
            }

            for (int k = 0; k < 8; k++)
            {
                if (isValid(mat, visited, i + row[k], j + col[k]))
                {
                    visited[i + row[k]][j + col[k]] = true;
                    q.add(new Node(i + row[k], j + col[k], dist + 1, node));
                }
            }
        }

        if (min_dist != Integer.MAX_VALUE) {
            return min_dist;
        }
        return -1;
    }
}