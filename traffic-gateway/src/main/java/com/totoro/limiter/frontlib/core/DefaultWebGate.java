package com.totoro.limiter.frontlib.core;


import com.totoro.limiter.response.CommonResponse;
import com.totoro.limiter.response.TrafficGateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class DefaultWebGate extends AbstractDefaultWebGate {
    private final WebClient webClient;
    public DefaultWebGate(){
        webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
    }
    @Override
    public boolean WG_IsNeedToWaiting(HttpServletRequest request, HttpServletResponse response, String gateId){
        String userId = WG_ReadCookie(request, "userId");

        if (userId == null) {
            userId = WG_GetRandomString(10);
            WG_WriteCookie(response, "userId", userId);
        }

        CommonResponse<TrafficGateResult> result = WG_CallLimiterApi(gateId, userId);
        if (result == null) {
            log.error("ERROR GATE_ID");
            return false;
        }

        TrafficGateResult data = result.getData();
        return data.getOrder() != 0;
    }


    /**
     * @param gateId
     * @param userId
     * @return
     *
     * 통신 뭐가 좋을지 고민
     */
    @Override
    public CommonResponse<TrafficGateResult> WG_CallLimiterApi(String gateId, String userId) {

        return webClient.get()
                .uri("/v1/api/gate/{gateId}/{userId}", gateId, userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResponse<TrafficGateResult>>() {})
                .block();

    }

}
