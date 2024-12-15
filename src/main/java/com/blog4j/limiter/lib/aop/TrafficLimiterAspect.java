package com.blog4j.limiter.lib.aop;

import com.blog4j.limiter.lib.annotation.TrafficLimiter;
import com.blog4j.limiter.lib.core.WebGate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class TrafficLimiterAspect {

    private final WebGate webGate;

    @Around(value = "@annotation(trafficLimiter)")
    public Object limitTraffic(ProceedingJoinPoint joinPoint, TrafficLimiter trafficLimiter) throws Throwable {
        final ServletRequestAttributes servletContainer = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request = Objects.requireNonNull(servletContainer).getRequest();
        final HttpServletResponse response = Objects.requireNonNull(servletContainer).getResponse();

        boolean needToWaiting = webGate.WG_IsNeedToWaiting(request, response, trafficLimiter.gateId());
        System.out.println("####### Waiting: "+ needToWaiting);

        if (needToWaiting) {
            return trafficLimiter.waitingPagePath();
        }

        return joinPoint.proceed();
    }

}
