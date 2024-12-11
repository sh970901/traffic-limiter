package com.blog4j.limiter;

import com.blog4j.limiter.frame.web.TrafficLimiterRunApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class TrafficLimiterApplication{

	public static void main(String[] args) {
		TrafficLimiterRunApplication.run(TrafficLimiterApplication.class, args);
	}

}
