package com.uga.mazesolvingagent.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SolveMazeRequest {
    private final String mazeId;
}
