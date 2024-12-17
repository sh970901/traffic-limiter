package com.blog4j.limiter.frame.config;

import com.blog4j.limiter.frame.config.property.RedisCacheProperties;
import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.lib.GateInfo;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    private final RedisCacheProperties redisCacheProperties;

    @Bean(name = "lettuceRedisClient")
    public RedisClient lettuceRedisClient() {
        return RedisClient.create("redis://"+ redisCacheProperties.getHost() + ":" + redisCacheProperties.getPort());
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
        for (GateInfo gateInfo : LimiterContext.gateInfos){
            dynamicPolicies.put(gateInfo.getGateId(), gateInfo.getBandwidth());
        }
//        dynamicPolicies.put("guest1", Bandwidth.classic(50, Refill.intervally(50, Duration.ofSeconds(1))));

        Bandwidth bandwidth = dynamicPolicies.getOrDefault(userGroup, Bandwidth.builder().capacity(3).refillGreedy(3, Duration.ofSeconds(1)).build());

        return BucketConfiguration.builder()
                                  .addLimit(bandwidth)
                                  .build();
    }

    @Bean
    public InitializingBean gateInfoInitializer() {
        /**
         * 설정 DB에서 받아오도록 수정 필요
         */
        List<GateInfo> gateInfos = new ArrayList<>();
        Bandwidth baobabtraffic3 =  Bandwidth.builder().capacity(3).refillGreedy(3, Duration.ofSeconds(1)).build();
        Bandwidth baobabtraffic30 =  Bandwidth.builder().capacity(30).refillGreedy(30, Duration.ofSeconds(1)).build();
        Bandwidth baobabtraffic50 =  Bandwidth.builder().capacity(50).refillGreedy(50, Duration.ofSeconds(1)).build();
        GateInfo gateInfo3 = GateInfo.from().gateId("baobabtraffic3").bandwidth(baobabtraffic3).build();
        GateInfo gateInfo30 = GateInfo.from().gateId("baobabtraffic30").bandwidth(baobabtraffic30).build();
        GateInfo gateInfo50 = GateInfo.from().gateId("baobabtraffic50").bandwidth(baobabtraffic50).build();
        gateInfos.add(gateInfo3);
        gateInfos.add(gateInfo30);
        gateInfos.add(gateInfo50);

        return ()-> LimiterContext.initGateInfos(gateInfos);
    }

}
