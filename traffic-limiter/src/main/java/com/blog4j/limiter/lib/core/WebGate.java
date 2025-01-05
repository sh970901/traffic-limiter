package com.blog4j.limiter.lib.core;

import com.blog4j.limiter.dto.LimiterResult;
import com.blog4j.limiter.frame.respose.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebGate {

    boolean WG_IsNeedToWaiting(HttpServletRequest request, HttpServletResponse response, String gateId);
    CommonResponse<LimiterResult> WG_CallLimiterApi(String gateId, String userId);

}
