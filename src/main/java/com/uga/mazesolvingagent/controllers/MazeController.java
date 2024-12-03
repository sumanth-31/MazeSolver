package com.uga.mazesolvingagent.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@AllArgsConstructor
public class MazeController {

    @Autowired
    private final MazeSolverService mazeSolverService;

    Map<String, Maze> mazes = new HashMap<>();

    @PostMapping("/create-maze")
    public ResponseEntity<CreatMazeResponse> createMaze(@RequestBody final CreatMazeRequest input) {
        Maze maze = new Maze(input.getGrid(), input.getUpwardWeights(), input.getRightwardWeights());
        mazes.put(maze.getMazeId(), maze);
        return ResponseEntity.ok().body(CreatMazeResponse.builder().mazeId(maze.getMazeId()).build());
    }
    
    @GetMapping("/solve-maze")
    public ResponseEntity<MazePathResponse> solveMaze(@RequestBody final SolveMazeRequest input) {
        Maze maze = mazes.get(input.getMazeId());
        Solution solution = mazeSolverService.solveMaze(maze);
        MazePathResponse mazePath = MazePathResponse.builder().path(solution.getPath())
                .pathCosts(solution.getPathCosts()).rhsValues(solution.getRhsValues())
                                    .nodesEvaluated(solution.getNodesEvaluated()).build();
        return ResponseEntity.ok().body(mazePath);
    }

    @PutMapping("/update-obstacle")
    public ResponseEntity<MazePathResponse> updateObstacle(@RequestBody final UpdateObstacleRequest input) {
        Maze maze = mazes.get(input.getMazeId());
        ObstacleUpdate update = com.uga.mazesolvingagent.models.dto.ObstacleUpdate.ADD.equals(input.getUpdate())
                ? ObstacleUpdate.ADD
                : ObstacleUpdate.REMOVE;
        Solution solution = mazeSolverService.updateObstacle(maze, input.getRowInd(), input.getColInd(), update);
        MazePathResponse mazePath = MazePathResponse.builder().path(solution.getPath())
                .pathCosts(solution.getPathCosts()).rhsValues(solution.getRhsValues())
                .nodesEvaluated(solution.getNodesEvaluated()).build();
        return ResponseEntity.ok().body(mazePath);
    }


}
