package com.blog4j.limiter.service;

import com.blog4j.limiter.frame.context.LimiterContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveRoomService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Boolean> isActiveUser(String gateId, String userId) {
        return reactiveRedisTemplate.hasKey(gateId + LimiterContext.ACTIVE_ROOM + userId);
    }
}
