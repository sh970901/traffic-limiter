package com.totoro.limiter.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totoro.limiter.service.TrafficGateInfoService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GateContext {


    public static Map<String, Bucket> gateBuckets = new ConcurrentHashMap<>();
    public static List<String> gateKeyPathList = new ArrayList<>();
    public static final String GATE_KEY_PREFIX = "config/traffic-gateway";
    public static final Long TOKEN_CONSUME_COUNT = 1L;

    private final ProxyManager<String> proxyManager;
    private final TrafficGateInfoService trafficGateInfoService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initGateBucketsValue() {
        Map<String, String> gateInfoMap = trafficGateInfoService.getGateInfoMap();
        for (String key : gateInfoMap.keySet()) {
            updateGateBuckets(gateInfoMap.get(key));
        }
        updateGateKeyPathList();
    }

    private void updateGateKeyPathList() {
        GateContext.gateKeyPathList = trafficGateInfoService.getGateInfoKeys();
    }

    public void updateGateBuckets(String gateInfoJsonValue){
        try {

            GateInfo gateInfo = objectMapper.readValue(gateInfoJsonValue, GateInfo.class);

            if(gateInfo.getGateId() == null){
                log.error("존재하지 않는 GateInfo: {}", gateInfo.toString());
            }

            // 기존 사용한 토큰 수 맞추기
            Bucket originBucket = GateContext.gateBuckets.getOrDefault(gateInfo.getGateId(), null);


            Bandwidth bandwidth;

            /**
             * 1. 이전 버킷이 없을 경우를 분리(초기 로딩 시는 null일 경우가 있음)
             * 2. 이전 버킷의 이용 가능한 토큰 수가 신규 토큰보다 더 적은 경우 토큰 수를 맞춰주고 점차 늘려줌
             * 3. 이전 버킷의 이용 가능한 토큰 수가 신규 토큰보다 더 많을 경우 신규 토큰수로 셋팅
             */
            if (originBucket == null){
                bandwidth = Bandwidth.builder()
                                     .capacity(gateInfo.getGateTps())
                                     .refillGreedy(gateInfo.getGateTps(), Duration.ofSeconds(1))
                                     .build();

            }
            else {
                long gateTps = gateInfo.getGateTps();
                long originCurrentTokens = originBucket.getAvailableTokens();
                if (gateTps <= originCurrentTokens){
                    bandwidth = Bandwidth.builder()
                                         .capacity(gateInfo.getGateTps())
                                         .refillGreedy(gateInfo.getGateTps(), Duration.ofSeconds(1))
                                         .build();

                }
                else {
                    bandwidth = Bandwidth.builder()
                                         .capacity(gateInfo.getGateTps())
                                         .refillGreedy(gateInfo.getGateTps(), Duration.ofSeconds(1))
                                         .initialTokens(originBucket.getAvailableTokens())
                                         .build();

                }
            }

            Supplier<BucketConfiguration> bucketConfigSupplier = () -> BucketConfiguration.builder()
                    .addLimit(bandwidth)
                    .build();

            proxyManager.removeProxy(gateInfo.getGateId());
            Bucket newBucket = proxyManager.builder().build(gateInfo.getGateId(), bucketConfigSupplier);


            GateContext.gateBuckets.put(gateInfo.getGateId(), newBucket);

            redisTemplate.delete(gateInfo.getGateId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
