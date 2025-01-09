package com.totoro.limiter.config;

import com.totoro.limiter.config.properties.ActiveRoomProperties;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
//@EnableAutoConfiguration(exclude={RedisAutoConfiguration.class})
public class ActiveRoomConfiguration {
    private final ActiveRoomProperties activeRoomProperties;

    @Bean
    public RedisConnectionFactory activeConnectionFactory() {
        DefaultClientResources clientResources = DefaultClientResources.builder()
                                                                       .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                                                            .clientResources(clientResources)
                                                                            .build();

        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(activeRoomProperties.getHost(), activeRoomProperties.getPort()), clientConfig);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        redisConnectionFactory.setExecutor(executor);

        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<?, ?> redisActiveTemplate(@Qualifier("activeConnectionFactory") RedisConnectionFactory activeConnectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(activeConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }
}
