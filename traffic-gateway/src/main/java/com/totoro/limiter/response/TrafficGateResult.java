package com.totoro.limiter.response;

import com.totoro.limiter.context.UserRoomContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "from")
public class TrafficGateResult {
    private Long order;
    private String message;

    public static TrafficGateResult pass(){
        return TrafficGateResult.from().message(UserRoomContext.PASS_MESSAGE).order(UserRoomContext.NO_EXIST).build();
    }

    public static TrafficGateResult wait(Long order){
        return TrafficGateResult.from().message(UserRoomContext.WAIT_MESSAGE).order(order).build();
    }
}
