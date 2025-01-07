package com.totoro.limiter.controller;

import com.sun.tools.javac.Main;
import com.totoro.limiter.context.GateContext;
import com.totoro.limiter.response.TrafficGateResult;
import com.totoro.limiter.service.TrafficGateService;
import com.totoro.limiter.service.WaitingRoomService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/gate")
@RequiredArgsConstructor
public class TrafficGateController {

    private final TrafficGateService trafficGateService;
    private final WaitingRoomService waitingRoomService;
    @GetMapping("/pin")
    public void pinned() throws InterruptedException {
        // Main.java
        int loopCount = Runtime.getRuntime().availableProcessors() * 2;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            Thread t = Thread.ofVirtual().start(() -> {
                synchronized (Main.class) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Sync");
                }
            });
            threads.add(t);
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }


    @GetMapping("/{gateId}/{userId}")
    public TrafficGateResult trafficLimiter(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        if(!isValidGateId(gateId)) return null;
        return trafficGateService.checkTraffic(gateId, userId);
    }


    // 조회 분리
    @GetMapping("/order/{gateId}/{userId}")
    public Long userOrder(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        return waitingRoomService.getUserRank(gateId, userId);
    }


    private boolean isValidGateId(String gateId) {
        return GateContext.gateBuckets.get(gateId) != null;
    }
}
