package com.blog4j.limiter.frame.context;

import com.blog4j.limiter.lib.GateInfo;
import com.blog4j.limiter.service.ConsulConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class GateContext {


    public static Map<String, Bucket> gateBuckets = new ConcurrentHashMap<>();
    public static List<String> gatePathList = new ArrayList<>();

    private final ProxyManager<String> proxyManager;
    private final ConsulConfigService consulConfigService;
    private final ObjectMapper objectMapper;

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initGateBucketsValue() {
        Map<String, String> gateInfoMap = consulConfigService.getGateInfoMap();
        for (String key : gateInfoMap.keySet()) {
            updateGateBuckets(gateInfoMap.get(key));
        }
        GateContext.gatePathList = consulConfigService.getGateInfoKeys();
    }

    public void updateGateBuckets(String gateInfoJsonValue){
        try {

            GateInfo gateInfo = objectMapper.readValue(gateInfoJsonValue, GateInfo.class);
            Bandwidth bandwidth = Bandwidth.builder().
                    capacity(gateInfo.getGateTps()).
                    refillGreedy(gateInfo.getGateTps(), Duration.ofSeconds(1))
                    .build();

            Supplier<BucketConfiguration> bucketConfigSupplier = () -> BucketConfiguration.builder()
                    .addLimit(bandwidth)
                    .build();
            proxyManager.removeProxy(gateInfo.getGateId());
            Bucket bucket = proxyManager.builder().build(gateInfo.getGateId(), bucketConfigSupplier);

            GateContext.gateBuckets.put(gateInfo.getGateId(), bucket);
            System.out.println(gateInfo.getGateId());
            redisTemplate.opsForValue().delete(gateInfo.getGateId()).block();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
