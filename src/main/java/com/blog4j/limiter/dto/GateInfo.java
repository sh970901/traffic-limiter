package com.blog4j.limiter.dto;

import io.github.bucket4j.Bandwidth;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "from")
public class GateInfo {

    private String gateId;
    private Bandwidth bandwidth;

}
