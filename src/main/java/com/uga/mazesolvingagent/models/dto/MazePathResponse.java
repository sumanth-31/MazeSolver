package com.uga.mazesolvingagent.models.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MazePathResponse {
    private final List<int[]> path;
    private final int[][] pathCosts;
    private final int[][] rhsValues;
    private final int nodesEvaluated;
}
