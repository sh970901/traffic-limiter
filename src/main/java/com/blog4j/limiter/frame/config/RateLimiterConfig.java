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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
@RefreshScope
public class RateLimiterConfig {

    @Value("${tps}")
    String trafficLimiterJsonData;

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


    public BucketConfiguration getBucketConfiguration(String gateId) {

        Bandwidth bandwidth = LimiterContext.gateBandwidth.getOrDefault(gateId, Bandwidth.builder().capacity(300).refillGreedy(300, Duration.ofSeconds(1)).build());

        return BucketConfiguration.builder()
                                  .addLimit(bandwidth)
                                  .build();
    }

    @Bean
    public InitializingBean gateInfoInitializer() {
        List<GateInfo> gateInfos = getGateInfos(trafficLimiterJsonData);
        return ()-> LimiterContext.initGateInfos(gateInfos);
    }


    public List<GateInfo> getGateInfos(String json)  {
        List<GateInfo> gateInfos = new ArrayList<>();

        JSONArray gateInfoJsonArray = getGateInfoJsonArray(json);

        for (Object obj : gateInfoJsonArray) {
            JSONObject gate = (JSONObject) obj;

            // 각 필드 값 추출
            String gateId = (String) gate.get("GateId");
            String gateName = (String) gate.get("GateName");
            Long tps = (Long) gate.get("GateTps");

            Bandwidth traffic =  Bandwidth.builder().capacity(tps).refillGreedy(tps, Duration.ofSeconds(1)).build();
//            GateInfo gateInfo = GateInfo.from().gateId(gateId).gateName(gateName).gateTps(tps).bandwidth(traffic).build();
            GateInfo gateInfo = GateInfo.from().gateId(gateId).gateName(gateName).gateTps(tps).build();
            gateInfos.add(gateInfo);
        }
        return gateInfos;
    }




    private JSONArray getGateInfoJsonArray(String json)  {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            jsonObject = (JSONObject) jsonParser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (JSONArray) jsonObject.get("GateInfo");
    }
}
