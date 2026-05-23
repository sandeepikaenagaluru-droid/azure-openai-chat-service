package com.chatservice.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    @Value("${rate-limit.requests-per-minute:10}")
    private int requestsPerMinute;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute,
                Refill.greedy(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder()
                     .addLimit(limit)
                     .build();
    }

    public boolean tryConsume(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());
        return bucket.tryConsume(1);
    }

    public long getRemainingTokens(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());
        return bucket.getAvailableTokens();
    }
}
