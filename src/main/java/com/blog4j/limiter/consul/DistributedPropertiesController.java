package com.blog4j.limiter.consul;

import com.blog4j.limiter.frame.config.RateLimiterConfig;
import com.blog4j.limiter.frame.context.GateContext;
import com.blog4j.limiter.frame.respose.CommonResponse;
import com.blog4j.limiter.service.ConsulConfigService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class DistributedPropertiesController {

    @Autowired
    RateLimiterConfig rateLimiterConfig;

    @Autowired
    ConsulConfigService consulConfigService;


    @GetMapping("/test3")
    public CommonResponse<Object> testsE3(){
        Map<String, String> gateInfoMap = consulConfigService.getGateInfoMap();

        gateInfoMap.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        return CommonResponse.fail("value");
    }

    @GetMapping("/test4")
    public CommonResponse<Object> testsE4(){

        GateContext.gateBuckets.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value.getAvailableTokens()));

        return CommonResponse.fail("value");
    }
}
