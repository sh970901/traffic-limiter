package com.blog4j.limiter.lib;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder(builderMethodName = "from")
@Jacksonized
@ToString
public class GateInfo {

    @JsonProperty("GateId")
    private String gateId;

    @JsonProperty("GateName")
    private String gateName;


    @JsonProperty("GateTps")
    private Long gateTps;

    @JsonProperty("ServiceId")
    private Integer serviceId;

}
