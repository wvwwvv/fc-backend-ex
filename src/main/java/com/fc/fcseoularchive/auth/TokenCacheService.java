package com.fc.fcseoularchive.auth;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "refreshTokens")
public class TokenCacheService {

    @CachePut(key = "#userId")
    public String saveRefreshToken(String userId, String refreshToken) {

        System.out.println("✅ 캐시 저장 - userId: " + userId); // 캐시 디버깅 용도
        System.out.println("✅ 캐시 저장 - refreshToken: " + refreshToken);
        return refreshToken;
    }

    @Cacheable(key = "#userId")
    public String getRefreshToken(String userId) {
        return null;  // 캐시 미스 시 null 반환
    }

}
