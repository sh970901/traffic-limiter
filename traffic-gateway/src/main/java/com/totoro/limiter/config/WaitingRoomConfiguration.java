package com.totoro.limiter.config;

import com.totoro.limiter.config.properties.WaitingRoomProperties;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@EnableAutoConfiguration(exclude={RedisAutoConfiguration.class})
@RequiredArgsConstructor
public class WaitingRoomConfiguration {
    private final WaitingRoomProperties waitingRoomProperties;

    @Bean
    public RedisConnectionFactory waitingConnectionFactory() {
        DefaultClientResources clientResources = DefaultClientResources.builder()
                                                                       .build();
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                                                            .clientResources(clientResources)
                                                                            .build();

        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(waitingRoomProperties.getHost(), waitingRoomProperties.getPort()), clientConfig);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        redisConnectionFactory.setExecutor(executor);

        return redisConnectionFactory;
    }



    @Bean
    @Primary
    public RedisTemplate<?, ?> redisWaitingTemplate(@Qualifier("waitingConnectionFactory") RedisConnectionFactory waitingConnectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(waitingConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }

}