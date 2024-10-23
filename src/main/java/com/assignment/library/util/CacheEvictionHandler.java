package com.assignment.library.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheEvictionHandler {
    private final CacheManager cacheManager;

    public CacheEvictionHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictCache(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        log.info("Evicting cache {} for key {}", cacheName, cacheKey);
        if (cache != null) {
            cache.evict(cacheKey);
        }
    }

}
