package com.blog4j.limiter.frame.config;


import com.blog4j.limiter.frame.config.property.RedisCacheProperties;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final RedisCacheProperties redisCacheProperties;

    @Bean("reactiveRedisConnectionFactory")
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(ClientResources clientResources) {
        LettuceClientConfiguration lettuceClientConfiguration =
            LettuceClientConfiguration.builder()
                                      .commandTimeout(Duration.ofSeconds(5))
                                      .shutdownTimeout(Duration.ZERO)
                                      .clientResources(clientResources)
                                      .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
            redisCacheProperties.getHost(), redisCacheProperties.getPort());
        redisStandaloneConfiguration.setPassword(""); // 비밀번호 설정 가능

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean(name = "reactiveRedisTemplate")
    @Primary
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        RedisSerializationContext serializationContext = RedisSerializationContext
            .<String, Object>newSerializationContext()
            .key(new StringRedisSerializer())
            .value(new Jackson2JsonRedisSerializer<>(Object.class))
            .hashKey(new StringRedisSerializer())
            .hashValue(new Jackson2JsonRedisSerializer<>(Object.class))
            .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

//    @Bean
//    public RedisTemplate<?, ?> redisCacheTemplate(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisCacheConnectionFactory) {
//        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisCacheConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//
//        return redisTemplate;
//    }

//    @Bean("redisCacheConnectionFactory")
//    public RedisConnectionFactory redisCacheConnectionFactory(ClientResources clientResources) {
//        //        https://programming-workspace.tistory.com/74 commondTimeout == connectionTimeout
//        LettuceClientConfiguration lettuceClientConfiguration =
//            LettuceClientConfiguration.builder()
//                                      .commandTimeout(Duration.ofSeconds(5))
//                                      .shutdownTimeout(Duration.ZERO)
//                                      .clientResources(clientResources())
//                                      .build();
//
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisCacheProperties.getHost(), redisCacheProperties.getPort());
//        redisStandaloneConfiguration.setPassword("");
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
//    }
//
//    @Bean
//    public CacheManager cacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisCacheConnectionFactory) {
//        //CacheManager Default 설정
//        RedisCacheConfiguration configuration =
//            RedisCacheConfiguration.defaultCacheConfig()
//                                   .disableCachingNullValues()
//                                   .entryTtl(Duration.ofSeconds(CacheExpireSec.DEFAULT_EXPIRE_SEC.getSec())) // default 만료 시간
//                                   .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                                   .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
//
//
//        return RedisCacheManager.RedisCacheManagerBuilder
//            .fromConnectionFactory(redisCacheConnectionFactory)
//            .cacheDefaults(configuration)
//            .build();
//    }
//

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }


    private enum CacheExpireSec {

        DEFAULT_EXPIRE_SEC(60 * 59);
        private final Integer sec;

        CacheExpireSec(Integer sec) {
            this.sec = sec;
        }

        public Integer getSec() {
            return sec;
        }
    }
}
