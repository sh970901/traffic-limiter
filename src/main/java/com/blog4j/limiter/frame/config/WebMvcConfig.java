package com.blog4j.limiter.frame.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 프로그램에서 제공하는 URL
                .allowedOriginPatterns("*")
                .allowedHeaders("*") // 어떤 헤더들을 허용할 것인지
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true)
                .allowedMethods(HttpMethod.GET.name(),
                    HttpMethod.HEAD.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.PUT.name(),
                    HttpMethod.PATCH.name(),
                    HttpMethod.DELETE.name(),
                    HttpMethod.OPTIONS.name());


    }
}
