package com.example.demo;

public class NodeLi {
    int x, y, dist;
    NodeLi prev;

    NodeLi(int x, int y, int dist, NodeLi prev)
    {
        this.x = x;
        this.y = y;
        this.dist = dist;
        this.prev = prev;
    }
}
