package com.uga.mazesolvingagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.uga.mazesolvingagent.models.domain.Maze;
import com.uga.mazesolvingagent.service.MazeSolverService;

@SpringBootApplication
public class MazeSolvingAgentApplication {

	public static void main(String[] args) {
        // MazeSolverService mazeSolverService = new MazeSolverService();
		// int[][] mazeGrid = new int[4][3];
        // int[][] upwardWeights = new int[][] {
        //         { 1, 9, 9 },
        //         { 9, 9, 1 },
        //         { 9, 9, 1 }
        // };
        // int[][] rightHandWeights = new int[][] {
        //         { 9, 9 },
        //         { 1, 1 },
        //         { 9, 9},
        //         { 9, 9 }
		// };
		// Maze maze = new Maze(mazeGrid, upwardWeights, rightHandWeights);
		// mazeSolverService.solveMaze(maze);
		// mazeSolverService.updateObstacle(maze, 2,2);
		SpringApplication.run(MazeSolvingAgentApplication.class, args);
	}

}
