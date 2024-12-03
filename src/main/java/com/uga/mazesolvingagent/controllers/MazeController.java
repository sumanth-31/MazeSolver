package com.uga.mazesolvingagent.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uga.mazesolvingagent.models.domain.Maze;
import com.uga.mazesolvingagent.models.domain.ObstacleUpdate;
import com.uga.mazesolvingagent.models.domain.Solution;
import com.uga.mazesolvingagent.models.dto.CreatMazeRequest;
import com.uga.mazesolvingagent.models.dto.CreatMazeResponse;
import com.uga.mazesolvingagent.models.dto.SolveMazeRequest;
import com.uga.mazesolvingagent.models.dto.UpdateObstacleRequest;
import com.uga.mazesolvingagent.models.dto.MazePathResponse;
import com.uga.mazesolvingagent.service.MazeSolverService;

import lombok.AllArgsConstructor;

/*
 * This class is the controller that defines all the APIs of the backend.
 */
@RestController
@AllArgsConstructor
public class MazeController {

    @Autowired
    private final MazeSolverService mazeSolverService;

    // Storage for the mazes created, which allows re-evaluating only required portions of a specific graph and also allows processing multiple graphs.
    Map<String, Maze> mazes = new HashMap<>();

    /*
    create-maze API allows creation of the maze environment with defined weights and obstacles. However, this API doesn't solve the maze.
    The API returns a mazeId which allows the user to interact with multiple graphs, for which the states are stored in the map above.
    */
    @PostMapping("/create-maze")
    public ResponseEntity<CreatMazeResponse> createMaze(@RequestBody final CreatMazeRequest input) {
        Maze maze = new Maze(input.getGrid(), input.getUpwardWeights(), input.getRightwardWeights());
        mazes.put(maze.getMazeId(), maze);
        return ResponseEntity.ok().body(CreatMazeResponse.builder().mazeId(maze.getMazeId()).build());
    }
    
    /*
     * solve-maze API evaluates the entire graph. This is used on the first call, when no information is available about the graph.
     * It returns the solution, the state of the graph and the number of nodes evaluated.
     */
    @PostMapping("/solve-maze")
    public ResponseEntity<MazePathResponse> solveMaze(@RequestBody final SolveMazeRequest input) {
        Maze maze = mazes.get(input.getMazeId());
        Solution solution = mazeSolverService.solveMaze(maze);
        MazePathResponse mazePath = MazePathResponse.builder().path(solution.getPath())
                .pathCosts(solution.getPathCosts()).rhsValues(solution.getRhsValues())
                .nodesEvaluated(solution.getNodesEvaluated()).build();
        return ResponseEntity.ok().body(mazePath);
    }

    /*
     * update-obstacle API allows updating the obstacles in a given maze. 
     * The API uses the existing information from previous traversals to cut down on the number of nodes evaluated.
     * It returns the solution, the state of the graph and the number of nodes evaluated.
     */
    @PutMapping("/update-obstacle")
    public ResponseEntity<MazePathResponse> updateObstacle(@RequestBody final UpdateObstacleRequest input) {
        Maze maze = mazes.get(input.getMazeId());
        Solution solution = mazeSolverService.updateObstacle(maze, input.getUpdates());
        MazePathResponse mazePath = MazePathResponse.builder().path(solution.getPath())
                .pathCosts(solution.getPathCosts()).rhsValues(solution.getRhsValues())
                .nodesEvaluated(solution.getNodesEvaluated()).build();
        return ResponseEntity.ok().body(mazePath);
    }


}
