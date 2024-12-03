package com.uga.mazesolvingagent.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatMazeRequest {
    private int[][] grid;
    private int[][] upwardWeights;
    private int[][] rightwardWeights;
}
