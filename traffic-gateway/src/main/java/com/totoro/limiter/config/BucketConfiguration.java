package com.totoro.limiter.config;

import com.totoro.limiter.config.properties.BucketProperties;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.resource.DefaultClientResources;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
public class BucketConfiguration {
    private final BucketProperties bucketProperties;

    @Bean(name = "lettuceRedisClient")
    public RedisClient lettuceRedisClient() {
        DefaultClientResources clientResources = DefaultClientResources.builder()
                                                                       .eventExecutorGroup(new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors(), Thread.ofVirtual().factory()))
                                                                       .build();
//        DefaultClientResources clientResources = DefaultClientResources.builder()
//                                                                       .build();

        return RedisClient.create(clientResources,
            "redis://" + bucketProperties.getHost() + ":" + bucketProperties.getPort());
    }

    @Bean
    public LettuceBasedProxyManager lettuceBasedProxyManager(RedisClient lettuceRedisClient) {

        StatefulRedisConnection<String, byte[]> connect = lettuceRedisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(connect)
                                       // 버킷 만료 정책(최대 용량으로 채워지는데 필요한 60초 동안 버킷으로 유지)
                                       .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(60)))
                                       .build();
    }
    @Bean
    public RedisTemplate<?, ?> redisTemplate (RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        DefaultClientResources clientResources = DefaultClientResources.builder()
                                                                       .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                                                            .clientResources(clientResources)
                                                                            .build();
        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(bucketProperties.getHost(), bucketProperties.getPort()), clientConfig);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        redisConnectionFactory.setExecutor(executor);
        return redisConnectionFactory;
    }
}
