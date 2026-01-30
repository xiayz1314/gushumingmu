package com.gushu.gushumingmu.Tree.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, Object> all();

    List<Map<String, Object>> speciesDistribution(int limit);

    List<Map<String, Object>> healthDistribution();

    List<Map<String, Object>> levelDistribution();

    List<Map<String, Object>> ageDistribution();

    List<Map<String, Object>> mapData();
}
