package com.blog4j.limiter.service;

import com.blog4j.limiter.context.GateContext;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsulConfigService {

    private final ConsulClient consulClient;
//    private final WebClient consulRestClient;
//    private final ObjectMapper objectMapper;

    /**
     * Gate Keys
     * @return
     */
    public List<String> getGateInfoKeys(){

        Response<List<String>> kvKeysOnly = consulClient.getKVKeysOnly(GateContext.GATE_KEY_PREFIX);
        return kvKeysOnly.getValue();
//        for (String key : value){
//            System.out.println(key);
//        }

        //        Response<List<GetValue>> kvValues = consulClient.getKVValues("/config/limiter-api/gate");
//        List<GetValue> getValues = kvValues.getValue();
//        for(GetValue value: getValues){
//            System.out.println(value.getKey()+":"+value.getDecodedValue());
//        }
//        return consulRestClient.get()
//                                        .uri("/v1/kv/config/limiter-api/gate/?keys")
//                                        .retrieve()
//                                        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
//                                        .block();
    }


    /*
     K: Key PATH
     Y: Gate Info
     */
    public Map<String, String> getGateInfoMap() {
        Map<String, String> gateInfoMap = new HashMap<>();

        Response<List<GetValue>> kvValues = consulClient.getKVValues("/config/limiter-api/gate");
        List<GetValue> getValues = kvValues.getValue();
        for(GetValue getValue: getValues){
            gateInfoMap.put(getValue.getKey(), getValue.getDecodedValue());
        }

        // 두 번 이상 호출되면 GateInfoMap static으로 선언해도 좋을 듯
//        List<String> gateInfoKeys = getGateInfoKeys();
//        Map<String, String> gateInfoMap = new HashMap<>();

//        for (String key : gateInfoKeys) {
//            String gateInfosJson = getGateInfoValues(key);
//            String gateValue = getGateValue(gateInfosJson);
//            gateInfoMap.put(key, gateValue);
//        }

        return gateInfoMap;
    }

//    private String getGateValue(String gateInfosJson){
//        String value = null;
//        try {
//            // JSON 응답 파싱
//            JsonNode rootNode = objectMapper.readTree(gateInfosJson);
//            if (rootNode.isArray() && !rootNode.isEmpty()) {
//                JsonNode valueNode = rootNode.get(0).get("Value");
//
//                if (valueNode != null && !valueNode.isNull()) {
//                    // Base64 디코딩
//                    String encodedValue = valueNode.asText();
//
//                    byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
//                    value = new String(decodedBytes);
//                }
//            }
//        } catch (Exception e) {
//            // exception 따로 처리하기
//            e.printStackTrace();
//            // 예외 처리 로직 추가
//        }
//        return value;
//    }
//
//    private String getGateInfoValues(String key){
//        return consulRestClient.get()
//                               .uri("/v1/kv/" + key)
//                               .retrieve()
//                               .bodyToMono(String.class)
//                               .block();
//    }

}
