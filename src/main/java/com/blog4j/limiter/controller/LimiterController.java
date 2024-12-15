package com.blog4j.limiter.controller;


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
    @GetMapping("/{userId}")
    public Mono<?> limiter(@PathVariable(name = "userId") String userId){
        return limiterService.limitTraffic(userId);
    }

    @GetMapping("/order/{userId}")
    public Long userOrder(@PathVariable(name = "userId") String userId){
        return limiterService.getWaitingUserOrder(userId);
    }

}
