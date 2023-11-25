package com.example.demo;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class AlgorithmDijkstra {

    Scanner sc = new Scanner(System.in);

    int nodes;
    Map map;
    int[][] mapa;
    private ArrayList<HashMap<Integer, Integer>> pList = new ArrayList<>();
    ArrayList<Integer> minH = new ArrayList<>();

    public AlgorithmDijkstra(Map map) {
        this.map = map;
        this.nodes = map.getHEIGHT()*map.getWIDTH();
        this.mapa = map.getMap();
    }

    public AlgorithmDijkstra() {
    }
    public void setPList() {
        Random random = new Random();
        for (int i = 0; i < nodes; i++) {
            pList.add(new HashMap<>());
        }

    }
    public void minFord() {
        minH.add(0);
        for (int i = 1; i < nodes; i++) {
            minH.add(100000);
        }
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (pList.get(i).containsKey(j+1)) {
                    if (minH.get(j) - minH.get(i) > pList.get(i).get(j+1)) {
                        minH.set(j, minH.get(i) + pList.get(i).get(j+1));
                    }
                }
            }
        }
        int k = 0;
        while (true) {
            k--;
            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < nodes; j++) {
                    if (pList.get(i).containsKey(j+1)) {
                        if (minH.get(j) - minH.get(i) > pList.get(i).get(j+1)) {
                            minH.set(j, minH.get(i) + pList.get(i).get(j+1));
                            k++;
                        }
                    }
                }
            }
            if (k <= 0)
                break;
        }

    }

}
