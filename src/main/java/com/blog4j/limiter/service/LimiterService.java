package com.blog4j.limiter.service;

import com.blog4j.limiter.context.GateContext;
import com.blog4j.limiter.context.LimiterContext;
import com.blog4j.limiter.dto.LimiterResult;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class LimiterService {

    private final WaitingRoomService waitingRoomService;
    private final ActiveRoomService activeRoomService;
    public Mono<LimiterResult> limitTraffic(String gateId, String userId) {

        /**
         * 1. 활성큐에 있는지
         * 2. 대기큐에 있는지
         * 3. 레이트리미터 적용
         */

        if (inActiveRoom(gateId, userId)) return Mono.just(LimiterResult.pass());

        // 대기큐에 값이 있다면
        if (inWaitingRoom(gateId)){
            /**
             *
             *  대기큐에 유저가 없다면 대기큐 추가
             *  이미 대기중이라면 기존 대기번호 리턴
             */
            Long order = userOrder(gateId, userId);

            if (Objects.equals(order, LimiterContext.NO_ORDER)){
                return waitingRoomService.registerWaitingRoom(gateId, userId).map(LimiterResult::wait);
            }
            else {
                return Mono.just(LimiterResult.wait(order));
            }
        }
        else {
            /**
             * 활성 큐에도 없고 대기큐에 아무 값도 없는 경우
             * RateLimiter 적용 후 넘치는 값을 대기큐로
             */

            boolean result = passThrough2RateLimiter(gateId);

            if (!result) {
                return waitingRoomService.registerWaitingRoom(gateId, userId).map(LimiterResult::wait);
            }
            return Mono.just(LimiterResult.pass());
        }
    }

    public boolean passThrough2RateLimiter(String gateId) {
        Bucket bucket = getBucket(gateId);
        ConsumptionProbe probe = consumeToken(bucket);
        loggingConsumption(gateId, probe);

        handleNotConsumed(probe);

        return probe.isConsumed();
    }

    private Bucket getBucket(String gateId) {
        return GateContext.gateBuckets.get(gateId);
    }

    private ConsumptionProbe consumeToken(Bucket bucket) {
        return bucket.tryConsumeAndReturnRemaining(LimiterContext.TOKEN_CONSUME_COUNT);
    }

    private void loggingConsumption(String gateId, ConsumptionProbe probe) {
        log.info("API Key: {}, RemoteAddress: {}, tryConsume: {}, remainToken: {}, tryTime: {}",
            gateId, gateId, probe.isConsumed(), probe.getRemainingTokens(), LocalDateTime.now());
    }

    private void handleNotConsumed(ConsumptionProbe probe) {
        if (!probe.isConsumed()) {
            log.info("#####################유입제어 걸림");
        }
    }

    public long getRemainToken(String gateId) {
        Bucket bucket = getBucket(gateId);
        return bucket.getAvailableTokens();
    }


    private boolean inActiveRoom(String gateId, String userId){
        return Boolean.TRUE.equals(activeRoomService.isActiveUser(gateId, userId).block());
    }


    private boolean inWaitingRoom(String gateId){
        return Boolean.TRUE.equals(waitingRoomService.isExistWaitingUser(gateId).block());
    }

    private Long userOrder(String gateId, String userId){
        return waitingRoomService.userOrder(gateId, userId).block();
    }

    public Long getWaitingUserOrder(String gateId, String userId) {
        return userOrder(gateId, userId);
    }
}
