package com.blog4j.limiter.consul;

import com.blog4j.limiter.frame.config.RateLimiterConfig;
import com.blog4j.limiter.frame.respose.CommonResponse;
import com.blog4j.limiter.service.ConsulConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class DistributedPropertiesController {

    @Value("${testKey}")
    String value;

    @Autowired
    RateLimiterConfig rateLimiterConfig;

    @Autowired
    ConsulConfigService consulConfigService;

    @GetMapping("/test2")
    public CommonResponse<Object> testsE(){
        rateLimiterConfig.getBucketConfiguration("11");

        return CommonResponse.fail(value);
    }

    @GetMapping("/test3")
    public CommonResponse<Object> testsE3(){
        consulConfigService.test();

        return CommonResponse.fail(value);
    }
}
