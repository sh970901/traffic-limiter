package com.blog4j.limiter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConsulConfigService {

    private final WebClient consulClient;
    private final ObjectMapper objectMapper;


    /**
     * Gate Keys
     * @return
     */
    private List<String> getGateInfoKeys(){
        return consulClient.get()
                                        .uri("http://localhost:8500/v1/kv/config/limiter-api/gate?keys")
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                                        .block();
    }

    private String getGateInfoValues(String key){
        return consulClient.get()
                .uri("http://localhost:8500/v1/kv/" + key)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /*
     K: Key PATH
     Y: Gate Info
     */
    public Map<String, String> getGateInfoMap() {

        // 두 번 이상 호출되면 GateInfoMap static으로 선언해도 좋을 듯
        List<String> gateInfoKeys = getGateInfoKeys();
        Map<String, String> gateInfoMap = new HashMap<>();

        for (String key : gateInfoKeys) {
            String gateInfosJson = getGateInfoValues(key);
            String gateValue = getGateValue(gateInfosJson);
            gateInfoMap.put(key, gateValue);
        }

        return gateInfoMap;
    }

    private String getGateValue(String gateInfosJson){
        String value = null;
        try {
            // JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(gateInfosJson);
            if (rootNode.isArray() && !rootNode.isEmpty()) {
                JsonNode valueNode = rootNode.get(0).get("Value");

                if (valueNode != null && !valueNode.isNull()) {
                    // Base64 디코딩
                    String encodedValue = valueNode.asText();

                    byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
                    value = new String(decodedBytes);
                }
            }
        } catch (Exception e) {
            // exception 따로 처리하기
            e.printStackTrace();
            // 예외 처리 로직 추가
        }
        return value;
    }


}
