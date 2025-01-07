package com.totoro.limiter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "server.redis.active")
@Configuration
public class ActiveRoomProperties extends ServerProperties{
}
