package com.blog4j.limiter;

import com.blog4j.limiter.frame.web.TrafficLimiterRunApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class TrafficLimiterApplication{

	/**
	 * 설정값들을 consul로 -> 설정 값 바뀔 수 있는 값
	 * -> 초당 요청 수, 진입한 사용자 PASS 시간 등 실시간으로 백오피스에서 업데이트하면 반영될 수 있도록
	 *
	 * 백오피스
	 * @param args
	 */
	public static void main(String[] args) {
		TrafficLimiterRunApplication.run(TrafficLimiterApplication.class, args);
	}

}
