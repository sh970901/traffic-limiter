package com.blog4j.limiter.consul;

import com.blog4j.limiter.frame.respose.CommonResponse;
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


    @GetMapping("/test2")
    public CommonResponse<Object> testsE(){
        System.out.println(value);
        return CommonResponse.fail(value);
    }
}
