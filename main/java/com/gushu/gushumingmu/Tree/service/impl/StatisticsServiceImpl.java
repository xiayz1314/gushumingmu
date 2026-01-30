package com.gushu.gushumingmu.Tree.service.impl;

import com.gushu.gushumingmu.Tree.mapper.AncientTreeMapper;
import com.gushu.gushumingmu.Tree.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final AncientTreeMapper mapper;

    @Override
    public Map<String, Object> all() {
        Map<String, Object> res = new HashMap<>();
        res.put("total", mapper.countAll());
        res.put("species", speciesDistribution(10));
        res.put("health", healthDistribution());
        res.put("level", levelDistribution());
        res.put("age", ageDistribution());
        return res;
    }

    @Override
    public List<Map<String, Object>> speciesDistribution(int limit) {
        return mapper.speciesDistribution(limit);
    }

    @Override
    public List<Map<String, Object>> healthDistribution() {
        return mapper.healthDistribution();
    }

    @Override
    public List<Map<String, Object>> levelDistribution() {
        return mapper.levelDistribution();
    }

    @Override
    public List<Map<String, Object>> ageDistribution() {
        return mapper.ageDistribution();
    }

    @Override
    public List<Map<String, Object>> mapData() {
        return mapper.mapData();
    }
}
