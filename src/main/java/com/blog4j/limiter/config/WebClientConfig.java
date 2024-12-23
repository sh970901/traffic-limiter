package com.blog4j.limiter.config;

import com.blog4j.limiter.config.property.ConsulHostProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ConsulHostProperties consulHostProperties;
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8081") // 기본 URL 설정
                .build();
    }

    @Bean
    public WebClient consulRestClient(WebClient.Builder builder) {
        return builder.baseUrl(consulHostProperties.getHost() + ":" + consulHostProperties.getPort()) // 기본 URL 설정
                      .build();
    }
}
