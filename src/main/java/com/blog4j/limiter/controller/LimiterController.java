package com.blog4j.limiter.controller;


import com.blog4j.limiter.dto.GateInfo;
import com.blog4j.limiter.dto.LimiterResult;
import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.service.LimiterService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/limiter")
@RequiredArgsConstructor
public class LimiterController {
     private final LimiterService limiterService;
    @GetMapping("/{gateId}/{userId}")
    public Mono<?> limiter(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        if(!isValidGateId(gateId)) return null;

        return limiterService.limitTraffic(gateId, userId);
    }

    // 조회 쪽은 따로 분리 예정
    @GetMapping("/order/{gateId}/{userId}")
    public Long userOrder(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        return limiterService.getWaitingUserOrder(gateId, userId);
    }

    private boolean isValidGateId(String gateId) {
        for (GateInfo gateInfo: LimiterContext.gateInfos)
            if (gateInfo.getGateId().equals(gateId)) {
                return true;
            }
        return false;
    }


}
