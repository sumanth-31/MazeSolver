package com.uga.mazesolvingagent.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatMazeResponse {
    private final String mazeId;
}
