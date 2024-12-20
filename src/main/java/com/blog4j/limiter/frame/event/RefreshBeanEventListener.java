package com.blog4j.limiter.frame.event;

import com.blog4j.limiter.frame.config.RateLimiterConfig;
import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.lib.GateInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshBeanEventListener {

    private final RateLimiterConfig rateLimiterConfig;
    @Value("${tps}")
    String trafficLimiterJsonData;
    private final Environment environment;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

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
    }
}
