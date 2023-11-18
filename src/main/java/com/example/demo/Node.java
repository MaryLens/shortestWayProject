package com.example.demo;

public class Node {
    int x, y, dist;
    Node prev;

    Node(int x, int y, int dist, Node prev)
    {
        this.x = x;
        this.y = y;
        this.dist = dist;
        this.prev = prev;
    }

}
