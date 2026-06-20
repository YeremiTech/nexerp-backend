package com.empresa.erp.auth.application.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private static final String KEY_PREFIX = "auth:revoked-access-token:";

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Instant> inMemoryRevokedTokens = new ConcurrentHashMap<>();

    public TokenRevocationService(ObjectProvider<StringRedisTemplate> redisTemplateProvider,
                                  @Value("${spring.cache.type:simple}") String cacheType) {
        this.redisTemplate = "redis".equalsIgnoreCase(cacheType)
                ? redisTemplateProvider.getIfAvailable()
                : null;
    }

    public void revoke(String tokenId, Instant expiresAt) {
        if (tokenId == null || tokenId.isBlank() || expiresAt == null) {
            return;
        }

        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }

        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(KEY_PREFIX + tokenId, "true", ttl);
            return;
        }

        inMemoryRevokedTokens.put(tokenId, expiresAt);
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) {
            return false;
        }

        if (redisTemplate != null) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + tokenId));
        }

        Instant expiresAt = inMemoryRevokedTokens.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            inMemoryRevokedTokens.remove(tokenId);
            return false;
        }
        return true;
    }
}
