package com.blog4j.limiter.controller;


import com.blog4j.limiter.frame.context.GateContext;
import com.blog4j.limiter.frame.context.LimiterContext;
import com.blog4j.limiter.lib.GateInfo;
import com.blog4j.limiter.service.LimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
        Long waitingUserOrder = limiterService.getWaitingUserOrder(gateId, userId);
        return waitingUserOrder;
    }

    private boolean isValidGateId(String gateId) {
        return true;
//        for (GateInfo gateInfo: GateContext.gateInfos)
//            if (gateInfo.getGateId().equals(gateId)) {
//                return true;
//            }
//        return false;
    }


}
