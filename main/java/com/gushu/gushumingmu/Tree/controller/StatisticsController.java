package com.gushu.gushumingmu.Tree.controller;

import com.gushu.gushumingmu.Tree.Dto.*;
import com.gushu.gushumingmu.Tree.service.StatisticsService;
import com.gushu.gushumingmu.User.Util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/ancient-trees/statistics")
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/all")
    public ResultUtil<StatisticsDTO> getAllStatistics() {
        return ResultUtil.success(statisticsService.getAllStatistics());
    }

    @GetMapping("/basic")
    public ResultUtil<BasicStatisticsDTO> getBasicStatistics() {
        return ResultUtil.success(statisticsService.getBasicStatistics());
    }

    @GetMapping("/species-distribution")
    public ResultUtil<Map<String, Object>> getSpeciesDistribution(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = Map.of(
                "data", statisticsService.getSpeciesDistribution(limit),
                "limit", limit
        );
        return ResultUtil.success(result);
    }

    @GetMapping("/health-distribution")
    public ResultUtil<Map<String, Integer>> getHealthDistribution() {
        return ResultUtil.success(statisticsService.getHealthDistribution());
    }

    @GetMapping("/level-distribution")
    public ResultUtil<Map<String, Integer>> getLevelDistribution() {
        return ResultUtil.success(statisticsService.getLevelDistribution());
    }

    @GetMapping("/age-distribution")
    public ResultUtil<Map<String, Object>> getAgeDistribution() {
        Map<String, Object> result = Map.of(
                "data", statisticsService.getAgeDistribution(),
                "ranges", new String[]{"0-100年", "100-300年", "300-500年", "500-1000年", "1000年以上"}
        );
        return ResultUtil.success(result);
    }

    @GetMapping("/species-age-comparison")
    public ResultUtil<Map<String, Object>> getSpeciesAgeComparison(
            @RequestParam(defaultValue = "8") int limit) {
        Map<String, Object> result = Map.of(
                "data", statisticsService.getSpeciesAgeComparison(limit),
                "limit", limit
        );
        return ResultUtil.success(result);
    }

    @GetMapping("/level-health-distribution")
    public ResultUtil<Map<String, Map<String, Integer>>> getLevelHealthDistribution() {
        return ResultUtil.success(statisticsService.getLevelHealthDistribution());
    }

    @GetMapping("/map-data")
    public ResultUtil<MapDataDTO> getMapData(@Valid @ModelAttribute MapQueryDTO queryDTO) {
        MapDataDTO result = statisticsService.getMapData(queryDTO);
        return ResultUtil.success(result);
    }


    @PostMapping("/clear-cache")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> clearCache() {
        statisticsService.clearStatisticsCache();
        return ResultUtil.success("缓存已清除");
    }
}