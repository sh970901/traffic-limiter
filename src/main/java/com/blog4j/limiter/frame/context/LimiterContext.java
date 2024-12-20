package com.blog4j.limiter.frame.context;

import com.blog4j.limiter.lib.GateInfo;

import com.blog4j.limiter.service.LimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LimiterContext {
    public static final Long NO_ORDER = 0L;
    public static final String NO_ORDER_MESSAGE = "접속 완료";
    public static final String ORDER_MESSAGE = "대기열 진입";
    public static final Long TOKEN_CONSUME_COUNT = 1L;

    public static final String WAITING_ROOM = ":Waiting-Room";
    public static final String ACTIVE_ROOM = ":Active_Room:";

    public static List<GateInfo> gateInfos = new ArrayList<>();
    public static Map<String, Bandwidth> gateBandwidth = new HashMap<>();
    public static Map<String, Bucket> gateBuckets = new ConcurrentHashMap<>();

    public static void initGateInfos(List<GateInfo> gateInfos){
        LimiterContext.gateInfos = gateInfos;

        Map<String, Bandwidth> bandwidths = new HashMap<>();
        Map<String, Bucket> buckets = new ConcurrentHashMap<>();

        for (GateInfo gateInfo : gateInfos){
            bandwidths.put(gateInfo.getGateId(), gateInfo.getBandwidth());
            // 버킷 맵 초기화를 동적으로 생성하도록
//            Bandwidth bandwidth = LimiterContext.gateBandwidth.getOrDefault(gateInfo.getGateId(), Bandwidth.builder().capacity(300).refillGreedy(300, Duration.ofSeconds(1)).build());
//
//            BucketConfiguration bucketConfig =  BucketConfiguration.builder()
//                                      .addLimit(bandwidth)
//                                      .build();
//
//            Bucket bucket = proxyManager.builder().build(gateInfo.getGateId(), bucketConfig);
//
//            buckets.put(gateInfo.getGateId(), bucket);
        }

        LimiterContext.gateBandwidth = bandwidths;
        LimiterContext.gateBuckets = buckets;

    }
}
