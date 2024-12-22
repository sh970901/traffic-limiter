package com.blog4j.limiter.frame.event;

import com.blog4j.limiter.frame.config.RateLimiterConfig;
import com.blog4j.limiter.frame.context.GateContext;
import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.lib.GateInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshBeanEventListener {

    private final RateLimiterConfig rateLimiterConfig;
    @Value("${tps}")
    String trafficLimiterJsonData;
    private final Environment environment;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final GateContext gateContext;

    @EventListener(EnvironmentChangeEvent.class)
    public void onRefresh(EnvironmentChangeEvent event) {
        if (event.getKeys().contains("tps")) {

            String updatedTpsValue = environment.getProperty("tps");

            if (updatedTpsValue.equals(trafficLimiterJsonData)) {
                return;
            }
            // 최신 값을 기반으로 GateInfos 생성
            List<GateInfo> gateInfos = rateLimiterConfig.getGateInfos(updatedTpsValue);

            // LimiterContext에 반영
            LimiterContext.initGateInfos(gateInfos);

            for (GateInfo gateInfo : gateInfos){
                redisTemplate.opsForValue().delete(gateInfo.getGateId()).block();
            }

        }

        for (String gateKey : GateContext.gateBuckets.keySet()){
            if (event.getKeys().contains(getLastPathComponent(gateKey))) {

                String updatedGateValue = environment.getProperty(getLastPathComponent(gateKey));
                gateContext.updateGateBuckets(gateKey, updatedGateValue);
//                GateInfo gateInfo = null;
//                try {
//                    gateInfo = objectMapper.readValue(updatedGateValue, GateInfo.class);
//                } catch (JsonProcessingException e) {
//                    throw new RuntimeException(e);
//                }
//                Bandwidth bandwidth = Bandwidth.builder().
//                        capacity(gateInfo.getGateTps()).
//                        refillGreedy(gateInfo.getGateTps(), Duration.ofSeconds(1))
//                        .build();
//
//                Supplier<BucketConfiguration> bucketConfigSupplier = () -> BucketConfiguration.builder()
//                        .addLimit(bandwidth)
//                        .build();
//
//                Bucket bucket = proxyManager.builder().build(gateInfo.getGateId(), bucketConfigSupplier);
//
//                GateContext.gateBuckets.put(gateKey, bucket);

            }
        }

    }
    private String getLastPathComponent(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        int lastSeparatorIndex = path.lastIndexOf('/');
        if (lastSeparatorIndex == -1) {
            // 구분자가 없는 경우 전체 문자열 반환
            return path;
        }
        // 마지막 구분자 이후의 부분 반환
        return path.substring(lastSeparatorIndex + 1);
    }
}
