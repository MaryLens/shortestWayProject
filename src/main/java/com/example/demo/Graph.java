package com.example.demo;

import java.util.ArrayList;
import java.util.List;


class Graph {
    private List<Node> nodes;

    public Graph(Mapaa map) {
        int[][] mat = map.getMap();
        nodes = new ArrayList<>();

        // Создаем узлы для каждой клетки карты
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] != 0) {
                    nodes.add(new Node(i, j));
                }
            }
        }

        // Добавляем соседей для каждого узла
        for (Node node : nodes) {
            int x = node.getX();
            int y = node.getY();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((x+i>=0&&y+j>=0)&&(i != 0 || j != 0)) {
                        int newX = x + i;
                        int newY = y + j;
                        if (isValidNeighbor(mat, x, y, newX, newY, map)) {
                            node.addNeighbor(getNodeByCoordinates(newX, newY));
                        }
                    }
                }
            }
        }
    }

    private boolean isValidNeighbor(int[][] mat, int x, int y, int newX, int newY, Mapaa map) {
        return newX >= 0 && newX < mat.length &&
                newY >= 0 && newY < mat[0].length &&
                mat[newX][newY] != 0 &&
                map.isValid(mat, new boolean[mat.length][mat[0].length], newX, newY, newX, newY);
    }

    private Node getNodeByCoordinates(int x, int y) {
        for (Node node : nodes) {
            if (node.getX() == x && node.getY() == y) {
                return node;
            }
        }
        return null;
    }

    public List<Node> getNodes() {
        return nodes;
    }

}

class Node {
    private int x;
    private int y;
    private List<Node> neighbors;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        neighbors = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
