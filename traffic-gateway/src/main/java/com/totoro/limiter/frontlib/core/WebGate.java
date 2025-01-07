package com.totoro.limiter.frontlib.core;

import com.totoro.limiter.response.CommonResponse;
import com.totoro.limiter.response.TrafficGateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebGate {

    boolean WG_IsNeedToWaiting(HttpServletRequest request, HttpServletResponse response, String gateId);
    CommonResponse<TrafficGateResult> WG_CallLimiterApi(String gateId, String userId);

}
