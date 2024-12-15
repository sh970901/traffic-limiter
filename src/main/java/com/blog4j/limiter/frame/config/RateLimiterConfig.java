package com.blog4j.limiter.frame.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class RateLimiterConfig {

    @Bean(name = "lettuceRedisClient")
    public RedisClient lettuceRedisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    @Bean
    public LettuceBasedProxyManager lettuceBasedProxyManager(RedisClient lettuceRedisClient) {

        StatefulRedisConnection<String, byte[]> connect = lettuceRedisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(connect)
                                       // 버킷 만료 정책(최대 용량으로 채워지는데 필요한 60초 동안 버킷으로 유지)
                                       .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(60)))
                                       .build();
    }

    /**
     * 동적 정책을 생성하기 위한 메서드.
     * @return 사용자별 Rate Limiting 정책을 반환
     */
    public BucketConfiguration getBucketConfiguration(String userGroup) {
        Map<String, Bandwidth> dynamicPolicies = new HashMap<>();

        // 사용자 그룹별 정책 // 이 부분을 설정 값에서 가져오면 좋을 듯
        dynamicPolicies.put("guest1", Bandwidth.classic(50, Refill.intervally(50, Duration.ofSeconds(1))));
        dynamicPolicies.put("guest2", Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(1))));
        dynamicPolicies.put("guest", Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(1))));

        Bandwidth bandwidth = dynamicPolicies.getOrDefault(userGroup, Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(1))));

        return BucketConfiguration.builder()
                                  .addLimit(bandwidth)
                                  .build();
    }
}
