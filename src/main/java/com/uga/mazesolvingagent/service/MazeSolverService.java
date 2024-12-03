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
import com.uga.mazesolvingagent.models.dto.ObstacleUpdateData;
import com.uga.mazesolvingagent.Constants;

/*
 * The main class that implements the LPA* algorithm to solve maze environments. It contains the business logic of the application.
 */
public class MazeSolverService {

    // The comparator that helps the priority queue order the nodes in the right order (predecessors before successors).
    private static final Comparator<Node> NODE_PRIORITIZER = (node1, node2) -> node1.getKey() - node2.getKey();

    /*
     * Updates the node's RHS value, and also inserts the node into the queue if it requires further processing (if it is inconsistent)
     * This method is only concerned with the RHS values of the nodes, and doesn't modify the G values.
     */
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
        }
    }

    /*
     * This is the main method that evaluates the graph (or the sub-graph required).
     */
    private int calculateShortestPath(final Maze maze, final PriorityQueue<Node> queue) {
        Node goalNode = maze.getGoalNode();
        Set<Node> nodesEvaluated = new HashSet<>();
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            nodesEvaluated.add(currentNode);
            if (currentNode.isObstacle()) {
                continue;
            }
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

    /*
     * This method re-builds the shortest path based on the information obtained when the shortest path was calculated.
     */
    private List<int[]> getPath(final Maze maze) {
        List<int[]> path = new ArrayList<>();
        Stack<Node> pathStack = new Stack<>();
        if (maze.getGoalNode().getG() >= Constants.MAX_VALUE) {
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
                // This is unexpected because this can only happen when a solution doesn't exist, which should've been detected before the loop. This code should not be triggered.
                throw new RuntimeException("Unexpected case occurred!");
            }
            currentNode = optimalNeighbor.getNode();
            pathStack.push(currentNode);
        }
        while (!pathStack.isEmpty()) {
            currentNode = pathStack.pop();
            path.add(new int[] { currentNode.getRowInd(), currentNode.getColInd() });
        }
        path.add(new int[] { maze.getGoalNode().getRowInd(), maze.getGoalNode().getColInd() });
        return path;
    }

    /*
     * This is the function that allows evaluating a small portion of the graph when obstacles are added or removed dynamically from the environment.
     */
    public Solution updateObstacle(final Maze maze, final List<ObstacleUpdateData> obstacleUpdateList) {
        List<Node> nodes = new ArrayList<>();
        for (ObstacleUpdateData obstacleUpdateData : obstacleUpdateList) {
            ObstacleUpdate update = com.uga.mazesolvingagent.models.dto.ObstacleUpdate.ADD
                    .equals(obstacleUpdateData.getUpdate()) ? ObstacleUpdate.ADD : ObstacleUpdate.REMOVE;
            maze.updateObstacle(obstacleUpdateData.getRowInd(), obstacleUpdateData.getColInd(), update);
            nodes.add(maze.getNode(obstacleUpdateData.getRowInd(), obstacleUpdateData.getColInd()));
        }
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_PRIORITIZER);
        for (final Node node : nodes) {
            List<NeighborNode> neighbors = maze.getNeighbors(node);
            neighbors.stream().forEach(neighborNode -> queue.add(neighborNode.getNode()));
        }
        int nodesEvaluated = calculateShortestPath(maze, queue);
        return Solution.builder().path(getPath(maze)).pathCosts(maze.getPathCosts()).rhsValues(maze.getRhsValues())
                .nodesEvaluated(nodesEvaluated).build();
    }

    /*
     * This is the business logic that evaluates a fresh graph where no information is available, i.e, for the first traversal of the graph.
     */
    public Solution solveMaze(final Maze maze) {
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_PRIORITIZER);
        queue.add(maze.getStartNode());
        int nodesEvaluated = calculateShortestPath(maze, queue);
        return Solution.builder().path(getPath(maze)).pathCosts(maze.getPathCosts()).rhsValues(maze.getRhsValues()).nodesEvaluated(nodesEvaluated).build();
    }
}
