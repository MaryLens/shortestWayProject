package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

import java.util.*;

public class AStar {
    Mapaa map;
    private  Graph graph;
    private boolean[][] path;
    private boolean[][] visited;
    private List<Integer[]> pathList = new ArrayList<>();
    public AStar(Mapaa map){
        this.map = map;
        this.graph = new Graph(map);
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
    public int aStar(int startX, int startY, int endX, int endY) {
        Node start = new Node(startX, startY);
        Node end = new Node(endX, endY);
        for (Node n: graph.getNodes()) {
            if (n.getX() == startX && n.getY() == startY){
                start = n;
            } else if (n.getX() == endX && n.getY() == endY){
                end = n;
            }
        }
        Map<Node, Integer> gScore = new HashMap<>();
        Map<Node, Integer> fScore = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));

        gScore.put(start, 0);
        fScore.put(start, getHeuristic(start, end));
        openSet.offer(start);

        for (Node node : graph.getNodes()) {
            if (node != start) {
                gScore.put(node, Integer.MAX_VALUE);
                fScore.put(node, Integer.MAX_VALUE);
            }
        }

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current == end) {
                reconstructPath(cameFrom, end);
                return 1;
            }

            for (Node neighbor : current.getNeighbors()) {
                visited[neighbor.getX()][neighbor.getY()] = true;
                int tentativeGScore = gScore.get(current) + getDistanceBetweenNodes(current, neighbor);

                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + getHeuristic(neighbor, end));

                    if (!openSet.contains(neighbor)) {
                        openSet.offer(neighbor);
                    }
                }
            }
        }

        System.out.println("No path found.");
        return -1;
    }

    private static int getHeuristic(Node node, Node end) {
        return Math.abs(node.getX() - end.getX()) + Math.abs(node.getY() - end.getY());
    }

    private  void reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            pathList.add(new Integer[]{current.getX(), current.getY()});
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);

    }

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
    private static int getDistanceBetweenNodes(Node node1, Node node2) {
        return 1;
    }
    void reset(){
        path = new boolean[map.getWIDTH()][map.getHEIGHT()];
        visited = new boolean[map.getWIDTH()][map.getHEIGHT()];
        pathList = new ArrayList<>();
    }
}
