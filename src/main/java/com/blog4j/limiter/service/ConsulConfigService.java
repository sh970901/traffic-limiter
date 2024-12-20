package com.blog4j.limiter.service;

import com.blog4j.limiter.dto.LimiterResult;
import com.blog4j.limiter.frame.respose.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsulConfigService {

    private final WebClient consulClient;

    public void test(){
        List<String> keys = consulClient.get()
                                        .uri("http://localhost:8500/v1/kv/config/limiter-api/gate?keys")
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                                        .block();

        for(String key : keys){
            System.out.println(key);
        }
//        System.out.println(keys.get(0));
    }


}
