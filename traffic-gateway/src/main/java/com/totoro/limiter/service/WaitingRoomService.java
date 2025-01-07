package com.totoro.limiter.service;

import com.totoro.limiter.context.UserRoomContext;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final RedisTemplate<String, String> redisWaitingTemplate;


    public Long getUserRank(String gateId, String userId){

        Long rank = redisWaitingTemplate.opsForZSet().rank(gateId + UserRoomContext.WAITING_ROOM, userId);
        if (rank == null) {
            return UserRoomContext.NO_EXIST;
        }
        return rank + 1;

    }
    public Boolean isExistWaitingUser(String gateId) {
        return redisWaitingTemplate.hasKey(gateId + UserRoomContext.WAITING_ROOM);
    }

    public Long registerWaitingRoom(final String gateId, final String userId){
        String waitingRoomKey = gateId + UserRoomContext.WAITING_ROOM;

        long now = nowInstantSecond();
        Boolean addWait = redisWaitingTemplate.opsForZSet().add(waitingRoomKey, userId, now);
        Long rank = redisWaitingTemplate.opsForZSet().rank(waitingRoomKey, userId);

        if (Boolean.FALSE.equals(addWait)) {
            // 이미 등록된 경우 로그 기록
            log.error("User {} is already registered in the waiting room for gate {}", userId, gateId);
        }

        return rank + 1;
    }

    private long nowInstantSecond(){
        return Instant.now().getEpochSecond();
    }
}
