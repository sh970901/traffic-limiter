package com.blog4j.limiter.frame.context;

import com.blog4j.limiter.dto.GateInfo;

import java.util.ArrayList;
import java.util.List;

public class LimiterContext {
    public static final Long NO_ORDER = 0L;
    public static final Long TOKEN_CONSUME_COUNT = 1L;

    public static final String WAITING_ROOM = ":Waiting-Room";
    public static final String ACTIVE_ROOM = ":Active_Room:";

    public static final List<GateInfo> gateInfos = new ArrayList<>();

    public static void initGateInfos(List<GateInfo> gateInfos){
        LimiterContext.gateInfos.addAll(gateInfos);
    }
}
