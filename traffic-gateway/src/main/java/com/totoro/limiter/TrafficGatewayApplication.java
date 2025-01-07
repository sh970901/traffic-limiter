package com.totoro.limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class TrafficGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficGatewayApplication.class, args);
	}

}
