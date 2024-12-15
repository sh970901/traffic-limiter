package com.blog4j.limiter.lib.core;

import com.blog4j.limiter.dto.LimiterResult;
import com.blog4j.limiter.frame.respose.CommonResponse;
import com.blog4j.limiter.frame.util.RandomStringGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebGate {
    private final WebClient webClient;
    public boolean isNeedToWaiting(HttpServletRequest request, HttpServletResponse response, String gateId){
        String userId = WG_ReadCookie(request, "userId");

        if (userId == null) {
            userId = WG_GetRandomString(10);
            WG_WriteCookie(response, "userId", userId);
        }

        CommonResponse<LimiterResult> result = WG_CallLimiterApi(gateId, userId);
        if (result == null) {
            log.error("ERROR GATE_ID");
            return false;
        }

        LimiterResult data = result.getData();
        return data.getOrder() != 0;
    }



    private CommonResponse<LimiterResult> WG_CallLimiterApi(String gateId, String userId) {
        return webClient.get()
                .uri("/api/v1/limiter/{gateId}/{userId}", gateId, userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResponse<LimiterResult>>() {})
                .block();

    }

    public String WG_GetRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        SecureRandom random = new SecureRandom();

        String chars[] = "1,2,3,4,5,6,7,8,A,B,C,D,E,F,G,H,J,K,L,M,N,P,Q,R,S,T,W,X,Y,Z".split(",");

        for (int i = 0; i < length; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }
    /* 쿠키 저장*/
    public void WG_WriteCookie(HttpServletResponse res, String key, String value) {
        // default cookie (auto domain)
        try {
            Cookie cookie = new Cookie(key, URLEncoder.encode(value, "UTF-8"));
            cookie.setMaxAge(86400 * 1);
            cookie.setPath("/");
            res.addCookie(cookie);
        } catch (Exception ex) {
            // skip
        }
    }

    public String WG_ReadCookie(HttpServletRequest req, String key) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(key)) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }



    private String generateUserId() {
        return RandomStringGenerator.generateRandomString(10);
    }
}
