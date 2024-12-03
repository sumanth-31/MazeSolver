package com.uga.mazesolvingagent.models.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateObstacleRequest {
    private final String mazeId;
    private final List<ObstacleUpdateData> updates;
}
