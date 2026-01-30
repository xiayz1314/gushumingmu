package com.gushu.gushumingmu.Tree.task;

import com.gushu.gushumingmu.Tree.service.StatisticsService;
import com.gushu.gushumingmu.Tree.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheCleanTask {

    private final StatisticsService statisticsService;
    private final RedisUtil redisUtil;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanCache() {
        log.info("开始清理缓存...");

        try {
            statisticsService.clearStatisticsCache();
            redisUtil.deleteByPattern("ancient-tree-detail:*");
            redisUtil.deleteByPattern("ancient-tree-list:*");
            redisUtil.deleteByPattern("map-data:*");

            log.info("缓存清理完成");
        } catch (Exception e) {
            log.error("清理缓存失败", e);
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanMapDataCache() {
        log.info("清理地图数据缓存...");
        try {
            redisUtil.deleteByPattern("map-data:*");
        } catch (Exception e) {
            log.error("清理地图数据缓存失败", e);
        }
    }
}