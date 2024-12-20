package com.blog4j.limiter.frame.context;

import com.blog4j.limiter.lib.GateInfo;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimiterContext {
    public static final Long NO_ORDER = 0L;
    public static final String NO_ORDER_MESSAGE = "접속 완료";
    public static final String ORDER_MESSAGE = "대기열 진입";
    public static final Long TOKEN_CONSUME_COUNT = 1L;

    public static final String WAITING_ROOM = ":Waiting-Room";
    public static final String ACTIVE_ROOM = ":Active_Room:";

    public static List<GateInfo> gateInfos = new ArrayList<>();
    public static Map<String, Bandwidth> gateBandwidth = new HashMap<>();

    public static void initGateInfos(List<GateInfo> gateInfos){
        LimiterContext.gateInfos = gateInfos;

        Map<String, Bandwidth> gateBandwidths = new HashMap<>();

        for (GateInfo gateInfo : LimiterContext.gateInfos){
            gateBandwidths.put(gateInfo.getGateId(), gateInfo.getBandwidth());
        }

        LimiterContext.gateBandwidth = gateBandwidths;
    }
}
