package com.blog4j.limiter.service;

import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.lib.GateInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Long> userOrder(String gateId, String userId){
        return reactiveRedisTemplate.opsForZSet()
                                    .rank(gateId + LimiterContext.WAITING_ROOM, userId)
                                    .map(i -> i >= 0 ? i + 1 : i)
                                    .defaultIfEmpty(LimiterContext.NO_ORDER); // 존재하지 않으면 -1 반환

    }
    public Mono<Long> registerWaitingRoom(final String gateId, final String userId){

        long now = nowInstantSecond();
        return reactiveRedisTemplate.opsForZSet()
                                    .add(gateId + LimiterContext.WAITING_ROOM, userId, now)
                                    .filter(i -> i)
                                    .switchIfEmpty(Mono.error(new Exception("already register user . . . .")))
                                    .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(gateId + LimiterContext.WAITING_ROOM, userId))
                                    .map(i -> i >= 0 ? i + 1 : i);
    }

    // 진입을 허용
    public Mono<Long> moveActiveRoom(final String gateId, final int count){

        return reactiveRedisTemplate.opsForZSet()
                                    .popMin(gateId + LimiterContext.WAITING_ROOM, count) // ZSet에서 멤버 제거
                                    .flatMap(member ->
                                        // 2. Active-Room에 Key-Value 형태로 추가하고 TTL 설정
                                        reactiveRedisTemplate.opsForValue()
                                                             .set(gateId + LimiterContext.ACTIVE_ROOM + member.getValue(), String.valueOf(member.getScore()), Duration.ofMinutes(5))
                                    )
                                    .count(); // 이동된 멤버 수 반환
    }

    public Mono<Boolean> isExistWaitingUser(String gateId) {
        return reactiveRedisTemplate.hasKey(gateId + LimiterContext.WAITING_ROOM);
    }

    /**
     * 배치는 따로 서버 분리 필요
     */
    @PostConstruct
    public void scheduler(){
        for (GateInfo gateInfo :LimiterContext.gateInfos){
            String gateId = gateInfo.getGateId();
//            startActiveQueueProcessor(5, 1, gateId);
        }
        startActiveQueueProcessor(2, 1, "baobabtraffic3");
        startActiveQueueProcessor(2, 1, "baobabtraffic30");
        startActiveQueueProcessor(2, 1, "baobabtraffic50");
    }

    private void startActiveQueueProcessor(int second, int users, String gateId) {

        // 1초마다 10건씩 이동
        Flux.interval(Duration.ofSeconds(second))
            .flatMap(tick -> moveActiveRoom(gateId, users)
                .doOnNext(count -> log.info("Moved " + count + " users to Active-Room"))
                .onErrorResume(error -> {
                    log.info("Error while moving users: {}", error.getMessage());
                    return Mono.empty();
                })
            )
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    private long nowInstantSecond(){
        return Instant.now().getEpochSecond();
    }

}
