package com.uga.mazesolvingagent.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

import com.uga.mazesolvingagent.models.domain.Maze;
import com.uga.mazesolvingagent.models.domain.NeighborNode;
import com.uga.mazesolvingagent.models.domain.Node;
import com.uga.mazesolvingagent.models.domain.ObstacleUpdate;
import com.uga.mazesolvingagent.models.domain.Solution;
import com.uga.mazesolvingagent.Constants;

public class MazeSolverService {

    private static final Comparator<Node> NODE_PRIORITIZER = (node1, node2) -> node1.getKey() - node2.getKey();

    private void updateNode(final Maze maze, final Node node, final PriorityQueue<Node> queue) {
        if (!node.equals(maze.getStartNode())) {
            List<NeighborNode> neighbors = maze.getNeighbors(node);
            int rhs = Constants.MAX_VALUE;
            for (NeighborNode neighbor : neighbors) {
                rhs = Math.min(rhs, neighbor.getNode().getG() + neighbor.getTravelCost());
            }
            node.setRhs(rhs);
        }
        queue.remove(node);
        if (!node.isConsistent()) {
            queue.add(node);
        } else {
            System.out.println("Finalized g = " + node.getG() + " for " + node.getRowInd() + " " + node.getColInd());
        }
    }

    private int calculateShortestPath(final Maze maze, final PriorityQueue<Node> queue) {
        Node goalNode = maze.getGoalNode();
        Set<Node> nodesEvaluated = new HashSet<>();
        while (!queue.isEmpty() || !goalNode.isConsistent()) {
            Node currentNode = queue.poll();
            nodesEvaluated.add(currentNode);
            if (currentNode.isObstacle()) {
                continue;
            }
            System.out.println("Processing node " + currentNode.getRowInd() + " " + currentNode.getColInd() + " g = "
                    + currentNode.getG() + " rhs = " + currentNode.getRhs());
            if (currentNode.getG() > currentNode.getRhs()) {
                currentNode.setG(currentNode.getRhs());
            } else {
                currentNode.setG(Constants.MAX_VALUE);
                updateNode(maze, currentNode, queue);
            }
            List<NeighborNode> neighbors = maze.getNeighbors(currentNode);
            neighbors.stream().forEach(neighborNode -> updateNode(maze, neighborNode.getNode(), queue));
        }
        return nodesEvaluated.size();
    }

    private List<int[]> getPath(final Maze maze) {
        List<int[]> path = new ArrayList<>();
        Stack<Node> pathStack = new Stack<>();
        if (maze.getGoalNode().getG() >= Constants.MAX_VALUE) {
            System.out.println("No Solution Exists");
            return path;
        }
        Node currentNode = maze.getGoalNode();
        while (!currentNode.equals(maze.getStartNode())) {
            List<NeighborNode> neighbors = maze.getNeighbors(currentNode);
            NeighborNode optimalNeighbor = null;
            for (final NeighborNode neighbor : neighbors) {
                if (optimalNeighbor == null || neighbor.getNode().getG() < optimalNeighbor.getNode().getG()) {
                    optimalNeighbor = neighbor;
                }
            }
            if (optimalNeighbor == null) {
                // This is unexpected because this can only happen when a solution doesn't exist, which should've been detected before the loop
                throw new RuntimeException("Unexpected case occurred!");
            }
            currentNode = optimalNeighbor.getNode();
            pathStack.push(currentNode);
        }
        System.out.println("Path:");
        while (!pathStack.isEmpty()) {
            currentNode = pathStack.pop();
            path.add(new int[] { currentNode.getRowInd(), currentNode.getColInd() });
            System.out.print("(" + currentNode.getRowInd() + ", " + currentNode.getColInd() + ") -> ");
        }
        path.add(new int[] { maze.getGoalNode().getRowInd(), maze.getGoalNode().getColInd() });
        System.out.print("(" + maze.getGoalNode().getRowInd() + "," + maze.getGoalNode().getColInd() + ")");
        System.out.println("Goal g = " + maze.getGoalNode().getG() + " rhs = " + maze.getGoalNode().getRhs());
        return path;
    }

    public Solution updateObstacle(final Maze maze, final int rowInd, final int colInd, final ObstacleUpdate update) {
        maze.updateObstacle(rowInd, colInd, update);
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_PRIORITIZER);
        Node node = maze.getNode(rowInd, colInd);
        List<NeighborNode> neighbors = maze.getNeighbors(node);
        neighbors.stream().forEach(neighborNode -> queue.add(neighborNode.getNode()));
        int nodesEvaluated = calculateShortestPath(maze, queue);
        return Solution.builder().path(getPath(maze)).pathCosts(maze.getPathCosts()).rhsValues(maze.getRhsValues()).nodesEvaluated(nodesEvaluated).build();
    }

    public Solution solveMaze(final Maze maze) {
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_PRIORITIZER);
        queue.add(maze.getStartNode());
        int nodesEvaluated = calculateShortestPath(maze, queue);
        return Solution.builder().path(getPath(maze)).pathCosts(maze.getPathCosts()).rhsValues(maze.getRhsValues()).nodesEvaluated(nodesEvaluated).build();
    }
}
