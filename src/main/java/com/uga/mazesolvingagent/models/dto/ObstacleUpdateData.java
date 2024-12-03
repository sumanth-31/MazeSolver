package com.uga.mazesolvingagent.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ObstacleUpdateData {
    private final int rowInd;
    private final int colInd;
    private final ObstacleUpdate update;
}
