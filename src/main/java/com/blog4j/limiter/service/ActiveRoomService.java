package com.blog4j.limiter.service;

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

    public Mono<Boolean> isActiveUser(String userId) {
        String activeRoomKey = "Active-Room:" + userId;
        return reactiveRedisTemplate.hasKey(activeRoomKey);
    }
}
