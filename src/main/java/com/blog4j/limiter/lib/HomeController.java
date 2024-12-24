package com.blog4j.limiter.lib;


import com.blog4j.limiter.lib.annotation.TrafficLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    @TrafficLimiter(waitingPagePath = "limiter/waiting", gateId = "1313131311")
    public String main(){
        return "limiter/home";
    }

}
