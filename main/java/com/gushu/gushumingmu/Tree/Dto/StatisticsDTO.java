package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    private BasicStatisticsDTO basicStatistics;
    private List<SpeciesCountDTO> speciesDistribution;
    private Map<String, Integer> healthDistribution;
    private Map<String, Integer> levelDistribution;
    private List<AgeRangeDTO> ageDistribution;
    private List<SpeciesAgeDTO> speciesAgeComparison;
    private Map<String, Map<String, Integer>> levelHealthDistribution;
}