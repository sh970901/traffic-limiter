package com.blog4j.limiter.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "server.host.redis.session")
@Configuration
public class RedisSessionProperties extends ServerProperties {

}
