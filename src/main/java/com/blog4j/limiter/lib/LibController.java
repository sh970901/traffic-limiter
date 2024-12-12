package com.blog4j.limiter.lib;


import com.blog4j.limiter.dto.LimiterResult;
import com.blog4j.limiter.frame.respose.CommonResponse;
import com.blog4j.limiter.frame.util.RandomStringGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpRequest;
import java.util.LinkedHashMap;

@Controller
@RequiredArgsConstructor
public class LibController {

    private final WebClient webClient;

    @GetMapping("/")
    public String main(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Cookie[] cookies = httpServletRequest.getCookies();
        String userId = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        // 쿠키에 userId가 없는 경우 새로 생성하여 클라이언트에 저장
        if (userId == null) {
            userId = generateUserId(); // 고유한 userId 생성
            Cookie userIdCookie = new Cookie("userId", userId);
            userIdCookie.setMaxAge(60 * 60); // 1시간 유지
            userIdCookie.setPath("/");
            httpServletResponse.addCookie(userIdCookie);
        }

        CommonResponse<LimiterResult> result = checkUserAccess(userId);
//        System.out.println(result.getData().getOrder());
        LimiterResult data = result.getData();
        System.out.println(data);
        if (!(data.getOrder() == 0)){
            return "limiter/waiting";
        }
        return "limiter/home";
    }

    private String generateUserId() {
        return RandomStringGenerator.generateRandomString(10);
//        return String.valueOf(System.currentTimeMillis());
    }


    private CommonResponse checkUserAccess(String userId) {
        return webClient.get()
                .uri("/api/v1/limiter/{userId}", userId) // URL에 PathVariable 설정
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResponse<LimiterResult>>() {})
                .block();

    }
}
