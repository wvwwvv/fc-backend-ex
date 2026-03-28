package com.fc.fcseoularchive.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {

    @Getter
    @RequiredArgsConstructor
    public enum CacheType {
        // 도네이션 포함한 선수 전체 정보 (30분, 최대 저장 개수 10개)
        // 최대 저장 개수 : key 의 종류 ex) 2025년, 2026년 두 allPlayers 정보를 캐시로 사용 : maximumSize 2 로 충분
        //ALL_PLAYERS("allPlayers", 1800, 10);
        ALL_PLAYERS("allPlayers", 10, 10),

        // 리프레시 토큰용도 추가 (1시간 = 3600초, 최대 유저 10000명)
        REFRESH_TOKENS("refreshTokens", 3600, 10_000);


        private final String cacheName;
        private final int expiredAfterWrite;
        private final int maximumSize;
    }

    @Bean
    public CacheManager cacheManger() {
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(cache -> new CaffeineCache(cache.getCacheName(),
                        Caffeine.newBuilder()
                                .recordStats() // 캐시 성능 지표 기록 (선택)
                                .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.SECONDS) // TTL 설정
                                .maximumSize(cache.getMaximumSize()) // 최대 개수 설정 (OOM 방지)
                                .build()))
                .collect(Collectors.toList());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}