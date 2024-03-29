package com.example.demo;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

import java.util.*;

public class AlgorithmDijkstra {
    private  Graph graph;
    private Mapaa map;
    private boolean[][] path;
    private boolean[][] visited;
    private List<Integer[]> pathList = new ArrayList<>();
    public AlgorithmDijkstra(Mapaa map){
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
    public int dijkstra(int startX, int startY, int endX, int endY) {
        Node start = new Node(startX, startY);
        Node end = new Node(endX, endY);
        for (Node n: graph.getNodes()) {
            if (n.getX() == startX && n.getY() == startY){
                start = n;
            } else if (n.getX() == endX && n.getY() == endY){
                end = n;
            }
        }
        Map<Node, Integer> distance = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        distance.put(start, 0);
        priorityQueue.offer(start);

        for (Node node : graph.getNodes()) {
            if (node != start) {
                distance.put(node, Integer.MAX_VALUE);
            }
        }

        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            for (Node neighbor : current.getNeighbors()) {
                int newDist = distance.get(current) + getDistanceBetweenNodes(current, neighbor);
                if (distance.containsKey(neighbor) && newDist < distance.get(neighbor)) {
                    visited[neighbor.getX()][neighbor.getY()] = true;
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    priorityQueue.offer(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Node current = end;
        if (distance.get(end) == null ||distance.get(end) == Integer.MAX_VALUE) {
            System.out.println("No path found.");
            return -1;
        }
        int dist = distance.get(end);
        while (current != null) {
            path.add(current);
            pathList.add(new Integer[]{current.getX(), current.getY()});
            current = previous.get(current);
        }
        Collections.reverse(path);
        return dist;

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
