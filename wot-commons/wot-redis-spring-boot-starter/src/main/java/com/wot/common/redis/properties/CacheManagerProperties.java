package com.wot.common.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "wot.cache-manager")
public class CacheManagerProperties {
    private List<CacheConfig> configs;


    public List<CacheConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<CacheConfig> configs) {
        this.configs = configs;
    }

    public static class CacheConfig {
        /**
         * cache key
         */
        private String key;
        /**
         * 过期时间，sec
         */
        private long second = 60;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getSecond() {
            return second;
        }

        public void setSecond(long second) {
            this.second = second;
        }
    }
}
