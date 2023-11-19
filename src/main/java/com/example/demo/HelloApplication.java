package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class HelloApplication extends Application {
    //map parameters
    private static final int WIDTH = 56;
    private static final int HEIGHT = 70;

    private int startX = -1;
    private int startY = -1;
    private int endX = -1;
    private int endY = -1;

    //map initialisation
    private int[][] map = new int[WIDTH][HEIGHT];
    private boolean[][] path = new boolean[WIDTH][HEIGHT];
    boolean[][] visited = new boolean[WIDTH][HEIGHT];
    List<Integer[]> pathList = new ArrayList<>();

    private void drawMap(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * 8, HEIGHT * 8);

        int cellSize = 8;

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == startX && y == startY) {
                    gc.setFill(javafx.scene.paint.Color.BLUE);
                } else if (x == endX && y == endY) {
                    gc.setFill(Color.RED);
                } else if (map[x][y] == 0) {
                    gc.setFill(Color.BLACK);
                } else if (path[x][y]) {
                    gc.setFill(Color.CHARTREUSE);
                } else if (visited[x][y]) {
                    gc.setFill(Color.YELLOW);
                } else {
                    gc.setFill(javafx.scene.paint.Color.WHITE);
                }
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    //start method for javaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Visualization");
        mapChis();

        Canvas centralCanvas = createCanvas();
        Canvas leftCanvas = createCanvas();
        Canvas rightCanvas = createCanvas();
//        Canvas canvas = new Canvas(WIDTH * 10, HEIGHT * 10);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        drawMap(gc);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            startX = -1;
            startY = -1;
            endX = -1;
            endY = -1;
            path = new boolean[WIDTH][HEIGHT];
            visited = new boolean[WIDTH][HEIGHT];
            pathList = new ArrayList<>();
            drawMap(centralCanvas.getGraphicsContext2D());
            drawMap(leftCanvas.getGraphicsContext2D());
            drawMap(rightCanvas.getGraphicsContext2D());
        });

//        Pane root = new Pane();
//        root.getChildren().add(canvas);
        HBox root = new HBox(8);
        root.getChildren().addAll(leftCanvas, centralCanvas, rightCanvas);
        root.getChildren().add(resetButton);

        centralCanvas.setOnMouseClicked(event -> handleCanvasClick(centralCanvas, event, leftCanvas, centralCanvas, rightCanvas));
        leftCanvas.setOnMouseClicked(event -> handleCanvasClick(leftCanvas, event, leftCanvas, centralCanvas, rightCanvas));
        rightCanvas.setOnMouseClicked(event -> handleCanvasClick(rightCanvas, event, leftCanvas, centralCanvas, rightCanvas));

//        canvas.setOnMouseClicked(event -> {
//            int x = (int) (event.getX() / 10);
//            int y = (int) (event.getY() / 10);
//
//            if (startX == -1) {
//                if (map[x][y] != 0) {
//                    startX = x;
//                    startY = y;
//                    drawMap(gc);
//                }
//            } else if (endX == -1) {
//                if (map[x][y] != 0) {
//                    endX = x;
//                    endY = y;
//                    drawMap(gc);
//                    int min_dist = findShortestPathLi(map, startX, startY, endX, endY);
//                    drawMap(gc);
//                    if (min_dist != -1) {
//                        int stepDelayMillis = 100;
//                        displayPathStepByStep(gc, stepDelayMillis);
//                        System.out.println("The shortest path from source to destination has length " + min_dist);
//                    } else {
//                        System.out.println("Destination cannot be reached from source");
//                    }
//                }
//            }
//        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Canvas createCanvas() {
        Canvas canvas = new Canvas(WIDTH * 8, HEIGHT * 8);
        drawMap(canvas.getGraphicsContext2D());
        return canvas;
    }

    private void handleCanvasClick(Canvas canvas, javafx.scene.input.MouseEvent event, Canvas leftCanvas, Canvas centralCanvas, Canvas rightCanvas) {
        int x = (int) (event.getX() / 8);
        int y = (int) (event.getY() / 8);

        if (startX == -1) {
            if (map[x][y] != 0) {
                startX = x;
                startY = y;
                drawMap(leftCanvas.getGraphicsContext2D());
                drawMap(rightCanvas.getGraphicsContext2D());
                drawMap(centralCanvas.getGraphicsContext2D());
            }
        } else if (endX == -1) {
            if (map[x][y] != 0) {
                endX = x;
                endY = y;
                drawMap(leftCanvas.getGraphicsContext2D());
                drawMap(rightCanvas.getGraphicsContext2D());
                drawMap(centralCanvas.getGraphicsContext2D());
                int min_dist;

                if (canvas.equals(leftCanvas)) {
                    min_dist = findShortestPathLi(map, startX, startY, endX, endY);
                } else if (canvas.equals(rightCanvas)) {
                    min_dist = findShortestPathLi(map, startX, startY, endX, endY);
                } else {
                    min_dist = findShortestPathLi(map, startX, startY, endX, endY);
                }

                drawMap(canvas.getGraphicsContext2D());

                if (min_dist != -1) {
                    int stepDelayMillis = 100;
                    displayPathStepByStep(canvas.getGraphicsContext2D(), stepDelayMillis);
                    System.out.println("The shortest path from source to destination has length " + min_dist);
                } else {
                    System.out.println("Destination cannot be reached from source");
                }
            }
        }
    }

    //to display path beautifully, algorithm Li
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

    //possible movements, 8 ways, coordinates according to the combination of pairs (row[i],col[i])
    private static final int[] row = {-1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[] col = {0, -1, -1, -1, 0, 1, 1, 1};

    //check if chosen move is valid
    private static boolean isValid(int[][] mat, boolean[][] visited, int x, int y) {
        return (x >= 0) && (x < mat.length) && (y >= 0) && (y < mat[0].length)
                && mat[x][y] == 1 && !visited[x][y];
    }

    //overloading of isValid method considering the direction of the road
    private static boolean isValid(int[][] mat, boolean[][] visited, int x, int y, int directionX, int directionY) {
        if ((x < 0) || (x >= mat.length) || (y < 0) || (y >= mat[0].length) || mat[x][y] == 0 || visited[x][y]) {
            return false;
        }
        //check if cell has only one direction
        //left
        else if (!(directionX == -1 && directionY == 0) && mat[x - directionX][y - directionY] == 4) {
            return false;
        } //left-up
        else if (!(directionX == -1 && directionY == -1) && mat[x - directionX][y - directionY] == 6) {
            return false;
        } //up
        else if (!(directionX == 0 && directionY == -1) && mat[x - directionX][y - directionY] == 2) {
            return false;
        } //right-up
        else if (!(directionX == 1 && directionY == -1) && mat[x - directionX][y - directionY] == 7) {
            return false;
        } //right
        else if (!(directionX == 1 && directionY == 0) && mat[x - directionX][y - directionY] == 5) {
            return false;
        } //right-down
        else if (!(directionX == 1 && directionY == 1) && mat[x - directionX][y - directionY] == 9) {
            return false;
        } //down
        else if (!(directionX == 0 && directionY == 1) && mat[x - directionX][y - directionY] == 3) {
            return false;
        } //left-down
        else if (!(directionX == -1 && directionY == 1) && mat[x - directionX][y - directionY] == 8) {
            return false;
        }
        return true;
    }

    //algorithm Li
    private int findShortestPathLi(int[][] mat, int i, int j, int x, int y) {
        if (mat == null || mat.length == 0 || mat[i][j] == 0 || mat[x][y] == 0) {
            return -1;
        }

        int M = mat.length;
        int N = mat[0].length;

        Queue<Node> q = new ArrayDeque<>();

        visited[i][j] = true;
        q.add(new Node(i, j, 0, null));

        int min_dist = Integer.MAX_VALUE;

        while (!q.isEmpty()) {
            Node node = q.poll();

            i = node.x;
            j = node.y;
            int dist = node.dist;

            if (i == x && j == y) {
                min_dist = dist;
                while (node != null) {
                    pathList.add(new Integer[]{node.x, node.y});
                    node = node.prev;
                }
                break;
            }

            for (int k = 0; k < 8; k++) {
                if (isValid(mat, visited, i + row[k], j + col[k], row[k], col[k])) {
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

    //main method
    public static void main(String[] args) {
        launch(args);
    }

    // map generated randomly
    private void map() {
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

    // map generated using Chisinau pototype
    private void mapChis() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = 0;
            }
        }
        map[19][0] = 1;
        map[20][0] = 1;
        map[27][0] = 1;
        map[28][0] = 1;
        map[29][0] = 1;
        map[30][0] = 1;
        map[31][0] = 1;
        map[32][0] = 1;
        map[33][0] = 1;
        map[34][0] = 1;
        map[35][0] = 1;
        map[36][0] = 1;
        map[37][0] = 1;
        map[38][0] = 1;
        map[39][0] = 1;
        map[40][0] = 1;
        map[41][0] = 1;
        map[42][0] = 1;
        map[43][0] = 1;
        map[44][0] = 1;

        map[18][1] = 1;
        map[21][1] = 1;
        map[22][1] = 1;
        map[23][1] = 1;
        map[24][1] = 1;
        map[26][1] = 1;
        map[30][1] = 1;
        map[45][1] = 1;

        map[18][2] = 1;
        map[23][2] = 1;
        map[26][2] = 1;
        map[30][2] = 1;
        map[44][2] = 1;
        map[46][2] = 1;
        map[47][2] = 1;
        map[48][2] = 1;
        map[49][2] = 1;
        map[50][2] = 1;

        map[18][3] = 1;
        map[23][3] = 1;
        map[24][3] = 1;
        map[25][3] = 1;
        map[26][3] = 1;
        map[29][3] = 1;
        map[43][3] = 1;
        map[46][3] = 1;
        map[50][3] = 1;

        map[18][4] = 1;
        map[23][4] = 1;
        map[26][4] = 1;
        map[29][4] = 1;
        map[42][4] = 1;
        map[46][4] = 1;
        map[50][4] = 1;

        map[17][5] = 1;
        map[23][5] = 1;
        map[26][5] = 1;
        map[29][5] = 1;
        map[41][5] = 1;
        map[46][5] = 1;
        map[50][5] = 1;

        map[14][6] = 1;
        map[15][6] = 1;
        map[16][6] = 1;
        map[23][6] = 1;
        map[24][6] = 1;
        map[25][6] = 1;
        map[26][6] = 1;
        map[29][6] = 1;
        map[41][6] = 1;
        map[46][6] = 1;
        map[50][6] = 1;

        map[13][7] = 1;
        map[17][7] = 1;
        map[18][7] = 1;
        map[23][7] = 1;
        map[26][7] = 1;
        map[29][7] = 1;
        map[41][7] = 1;
        map[46][7] = 1;
        map[50][7] = 1;

        map[12][8] = 1;
        map[19][8] = 1;
        map[23][8] = 1;
        map[25][8] = 1;
        map[26][8] = 1;
        map[29][8] = 1;
        map[41][8] = 1;
        map[46][8] = 1;
        map[50][8] = 1;

        map[11][9] = 1;
        map[14][9] = 1;
        map[20][9] = 1;
        map[24][9] = 1;
        map[27][9] = 1;
        map[28][9] = 1;
        map[30][9] = 1;
        map[40][9] = 1;
        map[46][9] = 1;
        map[49][9] = 1;
        map[50][9] = 1;

        map[10][10] = 1;
        map[12][10] = 1;
        map[13][10] = 1;
        map[15][10] = 1;
        map[21][10] = 1;
        map[22][10] = 1;
        map[24][10] = 1;
        map[25][10] = 1;
        map[26][10] = 1;
        map[31][10] = 1;
        map[32][10] = 1;
        map[33][10] = 1;
        map[40][10] = 1;
        map[46][10] = 1;
        map[48][10] = 1;

        map[9][11] = 1;
        map[16][11] = 1;
        map[17][11] = 1;
        map[23][11] = 1;
        map[27][11] = 1;
        map[34][11] = 1;
        map[40][11] = 1;
        map[46][11] = 1;
        map[47][11] = 1;

        map[8][12] = 1;
        map[18][12] = 1;
        map[19][12] = 1;
        map[20][12] = 1;
        map[22][12] = 1;
        map[28][12] = 1;
        map[35][12] = 1;
        map[36][12] = 1;
        map[37][12] = 1;
        map[38][12] = 1;
        map[39][12] = 1;
        map[40][12] = 1;
        map[41][12] = 1;
        map[42][12] = 1;
        map[45][12] = 1;
        map[46][12] = 1;

        map[7][13] = 1;
        map[17][13] = 1;
        map[21][13] = 1;
        map[29][13] = 1;
        map[40][13] = 1;
        map[43][13] = 1;
        map[44][13] = 1;
        map[46][13] = 1;

        map[6][14] = 1;
        map[8][14] = 1;
        map[9][14] = 1;
        map[10][14] = 1;
        map[11][14] = 1;
        map[12][14] = 1;
        map[13][14] = 1;
        map[17][14] = 1;
        map[20][14] = 1;
        map[22][14] = 1;
        map[23][14] = 1;
        map[30][14] = 1;
        map[40][14] = 1;
        map[42][14] = 1;
        map[46][14] = 1;

        map[3][15] = 1;
        map[5][15] = 1;
        map[10][15] = 1;
        map[14][15] = 1;
        map[16][15] = 1;
        map[18][15] = 1;
        map[20][15] = 1;
        map[24][15] = 1;
        map[25][15] = 1;
        map[30][15] = 1;
        map[39][15] = 1;
        map[40][15] = 1;
        map[41][15] = 1;
        map[43][15] = 1;
        map[46][15] = 1;

        map[4][16] = 1;
        map[10][16] = 1;
        map[15][16] = 1;
        map[19][16] = 1;
        map[26][16] = 1;
        map[31][16] = 1;
        map[32][16] = 1;
        map[38][16] = 1;
        map[43][16] = 1;
        map[46][16] = 1;

        map[3][17] = 1;
        map[5][17] = 1;
        map[10][17] = 1;
        map[14][17] = 1;
        map[16][17] = 1;
        map[18][17] = 8;
        map[20][17] = 1;
        map[25][17] = 8;
        map[26][17] = 1;
        map[33][17] = 1;
        map[37][17] = 1;
        map[43][17] = 1;
        map[46][17] = 1;

        map[3][18] = 1;
        map[6][18] = 1;
        map[9][18] = 1;
        map[13][18] = 1;
        map[17][18] = 1;
        map[21][18] = 1;
        map[24][18] = 8;
        map[27][18] = 1;
        map[33][18] = 1;
        map[36][18] = 1;
        map[43][18] = 1;
        map[46][18] = 1;

        map[3][19] = 1;
        map[7][19] = 1;
        map[8][19] = 1;
        map[12][19] = 1;
        map[16][19] = 8;
        map[18][19] = 1;
        map[20][19] = 7;
        map[23][19] = 3;
        map[27][19] = 1;
        map[31][19] = 1;
        map[32][19] = 1;
        map[33][19] = 1;
        map[34][19] = 1;
        map[35][19] = 1;
        map[43][19] = 1;
        map[46][19] = 1;

        map[3][20] = 1;
        map[4][20] = 9;
        map[7][20] = 1;
        map[9][20] = 1;
        map[11][20] = 1;
        map[15][20] = 8;
        map[19][20] = 1;
        map[23][20] = 8;
        map[28][20] = 1;
        map[30][20] = 1;
        map[33][20] = 1;
        map[43][20] = 1;
        map[46][20] = 1;

        map[3][21] = 1;
        map[5][21] = 9;
        map[7][21] = 1;
        map[10][21] = 1;
        map[14][21] = 8;
        map[18][21] = 7;
        map[20][21] = 1;
        map[22][21] = 8;
        map[29][21] = 1;
        map[34][21] = 1;
        map[43][21] = 1;
        map[46][21] = 1;

        map[2][22] = 1;
        map[6][22] = 1;
        map[9][22] = 1;
        map[11][22] = 1;
        map[13][22] = 8;
        map[17][22] = 7;
        map[21][22] = 1;
        map[28][22] = 1;
        map[29][22] = 1;
        map[34][22] = 1;
        map[43][22] = 1;
        map[46][22] = 1;

        map[1][23] = 1;
        map[3][23] = 1;
        map[5][23] = 1;
        map[7][23] = 9;
        map[9][23] = 1;
        map[12][23] = 1;
        map[16][23] = 7;
        map[20][23] = 8;
        map[22][23] = 1;
        map[27][23] = 1;
        map[29][23] = 1;
        map[35][23] = 1;
        map[43][23] = 1;
        map[46][23] = 1;

        map[0][24] = 1;
        map[4][24] = 1;
        map[8][24] = 9;
        map[11][24] = 3;
        map[13][24] = 1;
        map[15][24] = 7;
        map[19][24] = 8;
        map[23][24] = 1;
        map[26][24] = 1;
        map[29][24] = 1;
        map[35][24] = 1;
        map[43][24] = 1;
        map[46][24] = 1;

        map[5][25] = 1;
        map[7][25] = 1;
        map[9][25] = 9;
        map[11][25] = 8;
        map[14][25] = 1;
        map[18][25] = 8;
        map[23][25] = 1;
        map[25][25] = 1;
        map[29][25] = 1;
        map[36][25] = 1;
        map[44][25] = 1;
        map[46][25] = 1;

        map[6][26] = 1;
        map[10][26] = 1;
        map[13][26] = 7;
        map[15][26] = 1;
        map[17][26] = 8;
        map[23][26] = 1;
        map[24][26] = 1;
        map[29][26] = 1;
        map[36][26] = 1;
        map[45][26] = 1;
        map[46][26] = 1;

        map[7][27] = 1;
        map[9][27] = 8;
        map[11][27] = 9;
        map[13][27] = 2;
        map[16][27] = 1;
        map[22][27] = 1;
        map[25][27] = 1;
        map[29][27] = 1;
        map[37][27] = 1;
        map[44][27] = 1;
        map[47][27] = 1;

        map[8][28] = 1;
        map[12][28] = 1;
        map[15][28] = 3;
        map[17][28] = 1;
        map[21][28] = 1;
        map[26][28] = 1;
        map[29][28] = 1;
        map[37][28] = 1;
        map[44][28] = 1;
        map[48][28] = 1;

        map[9][29] = 1;
        map[11][29] = 7;
        map[13][29] = 1;
        map[15][29] = 8;
        map[18][29] = 1;
        map[20][29] = 1;
        map[26][29] = 1;
        map[30][29] = 1;
        map[38][29] = 1;
        map[44][29] = 1;
        map[49][29] = 1;
        map[9][29] = 1;

        map[10][30] = 1;
        map[14][30] = 8;
        map[19][30] = 1;
        map[27][30] = 1;
        map[30][30] = 1;
        map[38][30] = 1;
        map[44][30] = 1;
        map[50][30] = 1;

        map[11][31] = 1;
        map[13][31] = 8;
        map[15][31] = 1;
        map[16][31] = 1;
        map[18][31] = 1;
        map[20][31] = 1;
        map[27][31] = 1;
        map[29][31] = 1;
        map[39][31] = 1;
        map[44][31] = 1;
        map[50][31] = 1;

        map[12][32] = 1;
        map[16][32] = 1;
        map[17][32] = 1;
        map[21][32] = 1;
        map[28][32] = 1;
        map[39][32] = 1;
        map[44][32] = 1;
        map[50][32] = 1;

        map[11][33] = 3;
        map[13][33] = 6;
        map[14][33] = 4;
        map[16][33] = 1;
        map[18][33] = 1;
        map[20][33] = 1;
        map[21][33] = 1;
        map[22][33] = 1;
        map[23][33] = 1;
        map[24][33] = 1;
        map[25][33] = 1;
        map[26][33] = 1;
        map[27][33] = 1;
        map[28][33] = 1;
        map[29][33] = 1;
        map[40][33] = 1;
        map[43][33] = 1;
        map[51][33] = 1;

        map[11][34] = 3;
        map[15][34] = 1;
        map[19][34] = 1;
        map[30][34] = 1;
        map[40][34] = 1;
        map[42][34] = 1;
        map[52][34] = 1;

        map[11][35] = 8;
        map[15][35] = 1;
        map[17][35] = 5;
        map[18][35] = 5;
        map[19][35] = 1;
        map[31][35] = 1;
        map[41][35] = 1;
        map[52][35] = 1;

        map[10][36] = 3;
        map[13][36] = 1;
        map[14][36] = 1;
        map[16][36] = 1;
        map[19][36] = 1;
        map[32][36] = 1;
        map[42][36] = 1;
        map[52][36] = 1;

        map[10][37] = 3;
        map[12][37] = 1;
        map[16][37] = 1;
        map[19][37] = 1;
        map[33][37] = 1;
        map[43][37] = 1;
        map[52][37] = 1;

        map[10][38] = 1;
        map[11][38] = 1;
        map[17][38] = 1;
        map[19][38] = 1;
        map[34][38] = 1;
        map[35][38] = 1;
        map[44][38] = 1;
        map[52][38] = 1;

        map[8][39] = 8;
        map[9][39] = 4;
        map[11][39] = 2;
        map[16][39] = 1;
        map[19][39] = 1;
        map[34][39] = 1;
        map[36][39] = 1;
        map[44][39] = 1;
        map[52][39] = 1;

        map[7][40] = 3;
        map[11][40] = 2;
        map[16][40] = 1;
        map[19][40] = 1;
        map[34][40] = 1;
        map[37][40] = 1;
        map[45][40] = 1;
        map[52][40] = 1;

        map[8][41] = 8;
        map[10][41] = 7;
        map[16][41] = 1;
        map[19][41] = 1;
        map[34][41] = 1;
        map[38][41] = 1;
        map[45][41] = 1;
        map[52][41] = 1;

        map[6][42] = 1;
        map[10][42] = 2;
        map[16][42] = 1;
        map[20][42] = 1;
        map[33][42] = 1;
        map[39][42] = 1;
        map[46][42] = 1;
        map[52][42] = 1;

        map[5][43] = 3;
        map[7][43] = 9;
        map[10][43] = 2;
        map[15][43] = 1;
        map[20][43] = 1;
        map[33][43] = 1;
        map[38][43] = 1;
        map[40][43] = 1;
        map[47][43] = 1;
        map[53][43] = 1;

        map[5][44] = 3;
        map[8][44] = 5;
        map[5][44] = 3;
        map[15][44] = 1;
        map[20][44] = 1;
        map[33][44] = 1;
        map[38][44] = 1;
        map[40][44] = 1;
        map[48][44] = 1;
        map[49][44] = 1;
        map[53][44] = 1;

        map[5][45] = 8;
        map[9][45] = 2;
        map[15][45] = 1;
        map[20][45] = 1;
        map[33][45] = 1;
        map[34][45] = 5;
        map[35][45] = 9;
        map[37][45] = 1;
        map[41][45] = 1;
        map[50][45] = 1;
        map[53][45] = 1;

        map[4][46] = 8;
        map[6][46] = 1;
        map[9][46] = 2;
        map[15][46] = 1;
        map[20][46] = 1;
        map[33][46] = 1;
        map[36][46] = 1;
        map[42][46] = 1;
        map[51][46] = 1;
        map[53][46] = 1;

        map[3][47] = 8;
        map[7][47] = 1;
        map[8][47] = 1;
        map[9][47] = 2;
        map[14][47] = 1;
        map[20][47] = 1;
        map[25][47] = 1;
        map[26][47] = 1;
        map[27][47] = 1;
        map[28][47] = 1;
        map[29][47] = 1;
        map[30][47] = 1;
        map[31][47] = 1;
        map[32][47] = 1;
        map[33][47] = 1;
        map[35][47] = 1;
        map[37][47] = 1;
        map[43][47] = 1;
        map[51][47] = 1;
        map[53][47] = 1;

        map[0][48] = 1;
        map[1][48] = 1;
        map[2][48] = 1;
        map[9][48] = 1;
        map[14][48] = 1;
        map[20][48] = 1;
        map[25][48] = 1;
        map[32][48] = 1;
        map[34][48] = 1;
        map[38][48] = 1;
        map[44][48] = 1;
        map[52][48] = 1;

        map[3][49] = 1;
        map[8][49] = 7;
        map[10][49] = 1;
        map[14][49] = 1;
        map[21][49] = 1;
        map[25][49] = 1;
        map[32][49] = 1;
        map[35][49] = 1;
        map[39][49] = 1;
        map[45][49] = 1;
        map[53][49] = 1;

        map[4][50] = 1;
        map[5][50] = 1;
        map[7][50] = 7;
        map[11][50] = 1;
        map[14][50] = 1;
        map[22][50] = 1;
        map[23][50] = 1;
        map[25][50] = 1;
        map[26][50] = 4;
        map[27][50] = 4;
        map[28][50] = 4;
        map[29][50] = 4;
        map[30][50] = 4;
        map[31][50] = 4;
        map[32][50] = 1;
        map[33][50] = 5;
        map[34][50] = 5;
        map[35][50] = 1;
        map[36][50] = 5;
        map[37][50] = 5;
        map[38][50] = 5;
        map[39][50] = 1;
        map[46][50] = 1;
        map[54][50] = 1;
        map[55][50] = 1;

        map[6][51] = 1;
        map[12][51] = 1;
        map[14][51] = 1;
        map[24][51] = 1;
        map[25][51] = 1;
        map[32][51] = 1;
        map[36][51] = 1;
        map[46][51] = 1;

        map[7][52] = 1;
        map[13][52] = 1;
        map[25][52] = 1;
        map[26][52] = 1;
        map[32][52] = 1;
        map[37][52] = 1;
        map[46][52] = 1;

        map[8][53] = 1;
        map[9][53] = 1;
        map[12][53] = 1;
        map[25][53] = 1;
        map[27][53] = 1;
        map[32][53] = 1;
        map[37][53] = 1;
        map[45][53] = 1;
        map[47][53] = 1;

        map[10][54] = 1;
        map[11][54] = 1;
        map[25][54] = 1;
        map[28][54] = 1;
        map[32][54] = 1;
        map[38][54] = 1;
        map[44][54] = 1;
        map[48][54] = 1;

        map[12][55] = 1;
        map[25][55] = 1;
        map[29][55] = 1;
        map[32][55] = 1;
        map[37][55] = 1;
        map[39][55] = 1;
        map[43][55] = 1;
        map[49][55] = 1;

        map[13][56] = 1;
        map[14][56] = 1;
        map[25][56] = 1;
        map[30][56] = 1;
        map[31][56] = 1;
        map[37][56] = 1;
        map[40][56] = 1;
        map[42][56] = 1;
        map[50][56] = 1;

        map[15][57] = 1;
        map[25][57] = 1;
        map[30][57] = 1;
        map[31][57] = 1;
        map[36][57] = 1;
        map[41][57] = 1;
        map[49][57] = 1;
        map[51][57] = 1;

        map[16][58] = 1;
        map[25][58] = 1;
        map[29][58] = 1;
        map[32][58] = 1;
        map[35][58] = 1;
        map[40][58] = 1;
        map[42][58] = 1;
        map[49][58] = 1;
        map[52][58] = 1;

        map[17][59] = 1;
        map[26][59] = 1;
        map[28][59] = 1;
        map[33][59] = 1;
        map[34][59] = 1;
        map[39][59] = 1;
        map[43][59] = 1;
        map[49][59] = 1;
        map[53][59] = 1;

        map[18][60] = 1;
        map[27][60] = 1;
        map[33][60] = 1;
        map[34][60] = 1;
        map[38][60] = 1;
        map[44][60] = 1;
        map[49][60] = 1;
        map[50][60] = 1;
        map[51][60] = 1;
        map[52][60] = 1;
        map[53][60] = 1;

        map[19][61] = 1;
        map[26][61] = 1;
        map[28][61] = 1;
        map[32][61] = 1;
        map[35][61] = 1;
        map[37][61] = 1;
        map[45][61] = 1;
        map[46][61] = 1;
        map[47][61] = 1;
        map[48][61] = 1;
        map[54][61] = 1;
        map[55][61] = 1;

        map[20][62] = 1;
        map[21][62] = 1;
        map[25][62] = 1;
        map[29][62] = 1;
        map[31][62] = 1;
        map[36][62] = 1;
        map[44][62] = 1;

        map[22][63] = 1;
        map[24][63] = 1;
        map[30][63] = 1;
        map[33][63] = 1;
        map[34][63] = 1;
        map[35][63] = 1;
        map[37][63] = 1;
        map[43][63] = 1;

        map[23][64] = 1;
        map[29][64] = 1;
        map[31][64] = 1;
        map[32][64] = 1;
        map[38][64] = 1;
        map[42][64] = 1;

        map[24][65] = 1;
        map[28][65] = 1;
        map[39][65] = 1;
        map[41][65] = 1;

        map[25][66] = 1;
        map[27][66] = 1;
        map[31][66] = 1;
        map[32][66] = 1;
        map[33][66] = 1;
        map[34][66] = 1;
        map[35][66] = 1;
        map[36][66] = 1;
        map[37][66] = 1;
        map[38][66] = 1;
        map[39][66] = 1;
        map[40][66] = 1;

        map[26][67] = 1;
        map[30][67] = 1;
        map[41][67] = 1;

        map[27][68] = 1;
        map[29][68] = 1;
        map[42][68] = 1;

        map[28][69] = 1;
        map[43][69] = 1;
    }
}