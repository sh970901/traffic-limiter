package com.blog4j.limiter.controller;


import com.blog4j.limiter.dto.LimiterResult;
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
     private final List<String> gateIds = new ArrayList<>();
    @GetMapping("/{gateId}/{userId}")
    public Mono<?> limiter(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        if(!isValidGateId(gateId)) return null;

        return limiterService.limitTraffic(gateId, userId);
    }

    @GetMapping("/order/{gateId}/{userId}")
    public Long userOrder(@PathVariable(name = "gateId") String gateId, @PathVariable(name = "userId") String userId){
        return limiterService.getWaitingUserOrder(gateId, userId);
    }

    private boolean isValidGateId(String gateId) {
        return gateIds.contains(gateId);
    }
    @PostConstruct
    public void initGateIdsInHeap(){
        /**
         *  값을 받아와 초기 셋팅
         */
        gateIds.add("12345");
    }


}
