package com.uga.mazesolvingagent.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateObstacleRequest {
    private final String mazeId;
    private final int rowInd;
    private final int colInd;
    private final ObstacleUpdate update;
}
