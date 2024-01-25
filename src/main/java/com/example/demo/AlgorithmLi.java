package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AlgorithmLi {
    private Mapaa map;
    private boolean[][] path;
    private boolean[][] visited;
    private List<Integer[]> pathList = new ArrayList<>();

    public AlgorithmLi(Mapaa map) {
        this.map = map;
        path = new boolean[map.getWIDTH()][map.getHEIGHT()];
        visited = new boolean[map.getWIDTH()][map.getHEIGHT()];
    }

    public boolean[][] getPath() {
        return path;
    }

    public boolean[][] getVisited() {
        return visited;
    }

    public List<Integer[]> getPathList() {
        return pathList;
    }

    //algorithm Li
    int findShortestPathLi(int[][] mat, int i, int j, int x, int y) {
        int[] row = map.getRow();
        int[] col = map.getCol();
        if (mat == null || mat.length == 0 || mat[i][j] == 0 || mat[x][y] == 0) {
            return -1;
        }

        int M = mat.length;
        int N = mat[0].length;

        Queue<NodeLi> q = new ArrayDeque<>();

        visited[i][j] = true;
        q.add(new NodeLi(i, j, 0, null));

        int min_dist = Integer.MAX_VALUE;

        while (!q.isEmpty()) {
            NodeLi nodeLi = q.poll();

            i = nodeLi.x;
            j = nodeLi.y;
            int dist = nodeLi.dist;

            if (i == x && j == y) {
                min_dist = dist;
                while (nodeLi != null) {
                    pathList.add(new Integer[]{nodeLi.x, nodeLi.y});
                    nodeLi = nodeLi.prev;
                }
                break;
            }

            for (int k = 0; k < 8; k++) {
                if (map.isValid(mat, visited, i + row[k], j + col[k], row[k], col[k])) {
                    visited[i + row[k]][j + col[k]] = true;
                    q.add(new NodeLi(i + row[k], j + col[k], dist + 1, nodeLi));
                }
            }
        }

        if (min_dist != Integer.MAX_VALUE) {
            return min_dist;
        }
        return -1;
    }

    //to display path beautifully, algorithm Li
    void displayPathStepByStep(GraphicsContext gc, int stepDelayMillis) {
        if (pathList.size() > 0) {
            Integer[] pathCoords = pathList.remove(pathList.size() - 1);
            path[pathCoords[0]][pathCoords[1]] = true;
            map.drawMap(gc,visited,path);

            PauseTransition pause = new PauseTransition(Duration.millis(stepDelayMillis));
            pause.setOnFinished(event -> displayPathStepByStep(gc, stepDelayMillis));
            pause.play();
        }
    }

    void reset(){
        path = new boolean[map.getWIDTH()][map.getHEIGHT()];
        visited = new boolean[map.getWIDTH()][map.getHEIGHT()];
        pathList = new ArrayList<>();
    }
}
