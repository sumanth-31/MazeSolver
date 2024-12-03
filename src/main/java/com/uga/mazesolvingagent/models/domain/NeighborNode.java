package com.uga.mazesolvingagent.models.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NeighborNode {
    private final Node node;
    private final int travelCost;
}
