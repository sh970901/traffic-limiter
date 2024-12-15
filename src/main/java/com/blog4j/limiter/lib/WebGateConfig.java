package com.blog4j.limiter.lib;

import com.blog4j.limiter.lib.core.DefaultWebGate;
import com.blog4j.limiter.lib.core.WebGate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebGateConfig {

    @Bean
    @ConditionalOnMissingBean(WebGate.class)
    public WebGate defaultWebGate(WebClient webClient) {
        return new DefaultWebGate(webClient);
    }

    @Bean(name = "webGateClient")
    public WebClient webGateClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080") // 기본 URL 설정
                .build();
    }

//    @Bean
//    @Primary
//    public WebGate defaultWebGate2(WebClient webClient) {
//        return new DefaultWebGate2(webClient);
//    }
}
