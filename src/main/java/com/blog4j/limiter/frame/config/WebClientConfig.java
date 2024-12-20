package com.blog4j.limiter.frame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080") // 기본 URL 설정
                .build();
    }

    @Bean
    public WebClient consulClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8500") // 기본 URL 설정
                      .build();
    }
}
