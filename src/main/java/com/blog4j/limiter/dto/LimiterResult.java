package com.blog4j.limiter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "from")
public class LimiterResult {
    private Long order;
    private String message;

    public static LimiterResult pass(){
        return LimiterResult.from().message("접속 완료").order(0L).build();
    }

    public static LimiterResult wait(Long order){
        return LimiterResult.from().message("대기열").order(order).build();
    }
}
