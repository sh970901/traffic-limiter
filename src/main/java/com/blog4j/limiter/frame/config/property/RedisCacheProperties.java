package com.blog4j.limiter.frame.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.redis.cache")
@Configuration
public class RedisCacheProperties extends  CacheProperties{
}
