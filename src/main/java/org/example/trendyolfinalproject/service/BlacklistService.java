package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void add(String token, long expirationMillis) {
        long seconds = expirationMillis / 1000;
        redisTemplate.opsForValue().set(PREFIX + token, "blacklisted", seconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}

