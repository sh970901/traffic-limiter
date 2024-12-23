package com.blog4j.limiter.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.consul.ConsulAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "server.host.consul")
@Configuration
public class ConsulHostProperties extends ServerProperties{
}

