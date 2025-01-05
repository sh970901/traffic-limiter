package com.totoro.limiter.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class TrafficGateController {

    @GetMapping("/limiter")
    public void limiter(){
      log.info(String.valueOf(Thread.currentThread().isVirtual()));
    }
}
