package com.totoro.limiter.frontlib;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FrontController {
    @GetMapping("/front")
    @TrafficLimiter(waitingPagePath = "front/waiting", gateId = "1313131311")
    public String main(){
        return "front/home";
    }
}
