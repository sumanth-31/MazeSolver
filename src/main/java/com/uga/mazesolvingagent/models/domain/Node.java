package com.uga.mazesolvingagent.models.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Node {
    private int g;
    private int rhs;
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
