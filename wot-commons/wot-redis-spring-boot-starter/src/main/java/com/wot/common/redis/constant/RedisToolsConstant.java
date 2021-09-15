package com.wot.common.redis.constant;

/**
 * redis 工具常量
 */
public class RedisToolsConstant {

    private RedisToolsConstant() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * single Redis
     */
    public final static int SINGLE = 1;

    /**
     * Redis cluster
     */
    public final static int CLUSTER = 2;

    /**
     * 分布式redis锁前缀
     */
    public static final String LOCK_KEY_PREFIX = "LOCK_KEY:";
}
