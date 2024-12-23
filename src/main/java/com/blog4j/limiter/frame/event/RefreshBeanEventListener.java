package com.blog4j.limiter.frame.event;

import com.blog4j.limiter.frame.context.GateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshBeanEventListener {

    private final Environment environment;
    private final GateContext gateContext;

    @EventListener(EnvironmentChangeEvent.class)
    public void onRefresh(EnvironmentChangeEvent event) {

        for (String gateKey : GateContext.gatePathList){
            String formatKey = formatKey(gateKey, GateContext.GATE_KEY_PREFIX);

            if (event.getKeys().contains(formatKey)) {

                String updatedGateValue = environment.getProperty(formatKey);
                gateContext.updateGateBuckets(updatedGateValue);
            }
        }
    }
    private String getLastPathComponent(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        int lastSeparatorIndex = path.lastIndexOf('/');
        if (lastSeparatorIndex == -1) {
            // 구분자가 없는 경우 전체 문자열 반환
            return path;
        }
        // 마지막 구분자 이후의 부분 반환
        return path.substring(lastSeparatorIndex + 1);
    }

    public static String formatKey(String originalKey, String prefixToRemove) {
        String formattedKey = originalKey;

        // 접두사 제거
        if (formattedKey.startsWith(prefixToRemove)) {
            formattedKey = formattedKey.substring(prefixToRemove.length());
        }

        // 슬래시를 점으로 대체
        formattedKey = formattedKey.replace('/', '.');

        // 앞뒤 공백 제거
        formattedKey = formattedKey.trim();

        // 앞에 남아있는 점 제거
        if (formattedKey.startsWith(".")) {
            formattedKey = formattedKey.substring(1);
        }

        return formattedKey;
    }
}
