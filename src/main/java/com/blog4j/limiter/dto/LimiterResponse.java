package com.blog4j.limiter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "from")
public class LimiterResponse {
    private Long resultCode;
    private Long order;
    private String message;

    public static LimiterResponse success(){
        return LimiterResponse.from().resultCode(-1L).message("접속 완료").order(0L).build();
    }

    public static LimiterResponse wait(Long order){
        return LimiterResponse.from().resultCode(1L).message("대기열").order(order).build();
    }
}
