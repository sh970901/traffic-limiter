package com.totoro.limiter.scheduler;

import com.totoro.limiter.context.GateContext;
import com.totoro.limiter.context.UserRoomContext;
import com.totoro.limiter.service.TrafficGateInfoService;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

/**
 * Scheduler 분리 필요
 */
@RestController
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ActiveUserScheduler {

    private final RedisTemplate<String, String> redisWaitingTemplate;
    private final RedisTemplate<String, String> redisActiveTemplate;


    @Scheduled(fixedRate = 1000)
    public void scheduler(){
//        log.info(String.valueOf(Thread.currentThread().isVirtual()));
        for (String gateId : GateContext.gateBuckets.keySet()) {
            long availableTokens = GateContext.gateBuckets.get(gateId).getAvailableTokens();
            moveActiveRoom(gateId,1);
        }

    }
    public void moveActiveRoom(final String gateId, final int count){
        Set<TypedTuple<String>> typedTuples = redisWaitingTemplate.opsForZSet().popMin(gateId + UserRoomContext.WAITING_ROOM, count);
        for (TypedTuple<String> user : typedTuples) {
            String value = user.getValue();
            Double score = user.getScore();
            redisActiveTemplate.opsForValue()
                .set(gateId + UserRoomContext.ACTIVE_ROOM + value, String.valueOf(score), Duration.ofMinutes(5));
        }
    }


}
