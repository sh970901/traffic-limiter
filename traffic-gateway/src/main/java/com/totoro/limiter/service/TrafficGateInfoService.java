package com.totoro.limiter.service;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.totoro.limiter.context.GateContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficGateInfoService {

    private final ConsulClient consulClient;

    /**
     * Gate Keys
     * @return
     */
    public List<String> getGateInfoKeys(){

        Response<List<String>> kvKeysOnly = consulClient.getKVKeysOnly(GateContext.GATE_KEY_PREFIX);
        return kvKeysOnly.getValue();
    }


    /*
     K: Key PATH
     Y: Gate Info
     */
    public Map<String, String> getGateInfoMap() {
        Map<String, String> gateInfoMap = new HashMap<>();

//        Response<List<GetValue>> kvValues = consulClient.getKVValues("/config/traffic-gateway/gate");
        Response<List<GetValue>> kvValues = consulClient.getKVValues(GateContext.GATE_KEY_PREFIX);
        List<GetValue> getValues = kvValues.getValue();
        for(GetValue getValue: getValues){
            System.out.println(getValue.getKey());
            gateInfoMap.put(getValue.getKey(), getValue.getDecodedValue());
        }

        return gateInfoMap;
    }


}
