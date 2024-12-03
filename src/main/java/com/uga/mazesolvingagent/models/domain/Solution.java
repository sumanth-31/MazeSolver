package com.uga.mazesolvingagent.models.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Solution {
    private final int nodesEvaluated;
    private final List<int[]> path;
    private final int[][] pathCosts;
    private final int[][] rhsValues;
}
