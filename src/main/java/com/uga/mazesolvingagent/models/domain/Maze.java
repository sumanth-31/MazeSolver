package com.uga.mazesolvingagent.models.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

import com.uga.mazesolvingagent.Constants;

/*
 * This class captures the environment created and provides helper functions that allow retrieval of specific information from the environment.
 */
public class Maze {

    private static final int[][] DIRECTIONS = new int[][]{
            { 0, 1 }, // Right
            { 0, -1 }, //Left
            { 1, 0 }, // Up
            {-1, 0 } // Down
    };
    private Node[][] nodes;
    private int[][] upwardWeights;
    private int[][] rightwardWeights;
    private int m;
    private int n;
    @Getter
    private Node startNode;
    @Getter
    private Node goalNode;

    @Getter
    private final String mazeId;

    public Maze(final int[][] maze, final int[][] upwardWeights, final int[][] rightwardWeights) {
        this.m = maze.length;
        this.n = maze[0].length;

        if (upwardWeights.length > m - 1) {
            throw new RuntimeException("Upward weights can only be of size " + (m - 1));
        }
        if (rightwardWeights[0].length > n - 1) {
            throw new RuntimeException("RightHand weights can only be of size " + (n - 1));
        }
        this.mazeId = UUID.randomUUID().toString();
        this.upwardWeights = upwardWeights;
        this.rightwardWeights = rightwardWeights;
        this.nodes = new Node[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                nodes[i][j] = new Node(i, j, maze[i][j] == -1, m - 1, n - 1);
                nodes[i][j].setG(Constants.MAX_VALUE);
                nodes[i][j].setRhs(Constants.MAX_VALUE);
            }
        }
        startNode = nodes[0][0];
        goalNode = nodes[m - 1][n - 1];
        nodes[0][0].setRhs(0);
    }
    
    public List<NeighborNode> getNeighbors(final Node node) {
        List<NeighborNode> res = new ArrayList<>();

        int currentNodeRow = node.getRowInd();
        int currentNodeCol = node.getColInd();
        for (final int[] direction : DIRECTIONS) {
            int neighborRow = currentNodeRow + direction[0];
            int neighborCol = currentNodeCol + direction[1];
            if (neighborRow >= m || neighborRow < 0 || neighborCol >= n || neighborCol < 0) {
                continue;
            }
            Node neighbor = nodes[neighborRow][neighborCol];
            if (neighbor.isObstacle()) {
                continue; // Neighbor is an obstacle, can't pass through it.
            }
            int travelCost = 0;
            switch (direction[0]) {
                case 1:
                    travelCost = upwardWeights[currentNodeRow][currentNodeCol];
                    break;
                case -1:
                    travelCost = upwardWeights[currentNodeRow - 1][currentNodeCol];
                    break;
                default:
                    break;
            }
            switch (direction[1]) {
                case 1:
                    travelCost = rightwardWeights[currentNodeRow][currentNodeCol];
                    break;
                case -1:
                    travelCost = rightwardWeights[currentNodeRow][currentNodeCol - 1];
                    break;
                default:
                    break;
            }
            res.add(NeighborNode.builder().node(neighbor).travelCost(travelCost).build());
        }
        return res;
    }
    
    public void updateObstacle(final int rowInd, final int colInd, final ObstacleUpdate update) {
        Node node = nodes[rowInd][colInd];
        node.setObstacle(ObstacleUpdate.ADD.equals(update));
        if (node.isObstacle()) {
            node.setG(Constants.MAX_VALUE);
        }
    }

    public Node getNode(final int rowInd, final int colInd) {
        return nodes[rowInd][colInd];
    }

    public int[][] getPathCosts() {
        int[][] pathCosts = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (nodes[i][j].isObstacle()) {
                    pathCosts[i][j] = -1;
                } else {
                    pathCosts[i][j] = nodes[i][j].getG();
                }
            }
        }
        return pathCosts;
    }

    public int[][] getRhsValues() {
        int[][] rhsValues = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (nodes[i][j].isObstacle()) {
                    rhsValues[i][j] = -1;
                } else {
                    rhsValues[i][j] = nodes[i][j].getRhs();
                }
            }
        }
        return rhsValues;
    }
}
