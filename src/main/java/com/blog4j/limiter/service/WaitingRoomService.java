package com.blog4j.limiter.service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Long> userOrder(String userId){
        return reactiveRedisTemplate.opsForZSet()
                                    .rank("Waiting-Room", userId)
                                    .map(i -> i >= 0 ? i + 1 : i)
                                    .defaultIfEmpty(-1L); // 존재하지 않으면 -1 반환

    }
    public Mono<Long> registerWaitingRoom(final String userId){

        long now = nowInstantSecond();

        return reactiveRedisTemplate.opsForZSet()
                                    .add("Waiting-Room", userId, now)
                                    .filter(i -> i)
                                    .switchIfEmpty(Mono.error(new Exception("already register user . . . .")))
                                    .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank("Waiting-Room", userId))
                                    .map(i -> i >= 0 ? i + 1 : i);
    }

    // 진입을 허용
    public Mono<Long> moveActiveRoom(final int count){

        return reactiveRedisTemplate.opsForZSet()
                                    .popMin("Waiting-Room", count) // ZSet에서 멤버 제거
                                    .flatMap(member ->
                                        // 2. Active-Room에 Key-Value 형태로 추가하고 TTL 설정
                                        reactiveRedisTemplate.opsForValue()
                                                             .set("Active-Room:" + member.getValue(), String.valueOf(member.getScore()), Duration.ofMinutes(5))
                                    )
                                    .count(); // 이동된 멤버 수 반환
    }

    public Mono<Boolean> isExistWaitingUser() {
        String waitingRoomKey = "Waiting-Room";
        return reactiveRedisTemplate.hasKey(waitingRoomKey);
    }

    @PostConstruct
    public void scheduler(){
        startActiveQueueProcessor(10, 1);
    }

    private void startActiveQueueProcessor(int second, int users) {
        // 1초마다 10건씩 이동
        Flux.interval(Duration.ofSeconds(second))
            .flatMap(tick -> moveActiveRoom(users)
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
