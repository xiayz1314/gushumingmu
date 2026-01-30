package com.gushu.gushumingmu.Tree.constant;

/**
 * 缓存常量
 */
public class CacheConstants {

    /**
     * 统计信息缓存名称
     */
    public static final String STATISTICS_CACHE = "statistics";

    /**
     * 古树详情缓存名称
     */
    public static final String ANCIENT_TREE_DETAIL_CACHE = "ancient-tree-detail";

    /**
     * 古树列表缓存名称
     */
    public static final String ANCIENT_TREE_LIST_CACHE = "ancient-tree-list";

    /**
     * 地图数据缓存名称
     */
    public static final String MAP_DATA_CACHE = "map-data";

    /**
     * 缓存过期时间（分钟）
     */
    public static final long CACHE_EXPIRE_MINUTES = 10;

    /**
     * 构建缓存key
     */
    public static String buildKey(String cacheName, String key) {
        return String.format("%s:%s", cacheName, key);
    }
}