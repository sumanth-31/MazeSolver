package com.uga.mazesolvingagent.models.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/*
 * Represents a node in the maze graph. Also has helper functions to retrieve specific information about the node.
 */
@Data
public class Node {
    private int g; // The path-cost from the start node to the current node.
    private int rhs; // RHS value represents the look-ahead cost from the LPA* algorithm. It represents the shortest possible value for g, and is used for evaluation at run-time.
    private boolean isObstacle;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int heuristic;
    private final int rowInd;
    private final int colInd;

    public Node(final int rowInd, final int colInd, final boolean isObstacle, final int goalRowInd, final int goalColInd) {
        this.rowInd = rowInd;
        this.colInd = colInd;
        this.isObstacle = isObstacle;
        this.heuristic = getManhattanDistance(rowInd, goalRowInd, colInd, goalColInd);
    }

    public int getKey() {
        int minGRhs = Math.min(g, rhs);
        return minGRhs + heuristic;
    }

    // Manhattan distance is the heuristic used in this implementation.
    private int getManhattanDistance(final int x1, final int x2, final int y1, final int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public boolean isConsistent() {
        return g == rhs;
    }

    public String toString() {
        return "Node(" + rowInd + "," + colInd + ")";
    }
}
