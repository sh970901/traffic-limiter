package com.totoro.limiter.service;

import com.totoro.limiter.context.UserRoomContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveRoomService {
    private final RedisTemplate<String, String> redisActiveTemplate;

    public Boolean isActiveUser(String gateId, String userId) {
        return redisActiveTemplate.hasKey(gateId + UserRoomContext.ACTIVE_ROOM + userId);
    }
}
