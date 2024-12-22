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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class GateContext {


    public static Map<String, Bucket> gateBuckets = new ConcurrentHashMap<>();

    private final ProxyManager<String> proxyManager;
    private final ConsulConfigService  consulConfigService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initGateBucketsValue() {
        Map<String, String> gateInfoMap = consulConfigService.getGateInfoMap();
        for (String key : gateInfoMap.keySet()) {
            updateGateBuckets(key, gateInfoMap.get(key));
        }
    }

    public void updateGateBuckets(String key, String gateInfoJsonValue){
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

            GateContext.gateBuckets.put(key, bucket);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
