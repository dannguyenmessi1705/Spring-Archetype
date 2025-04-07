package com.didan.archetype.factory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class LogSystemFactory {
    private String applicationCode;
    private String serviceCode;
    private long threadID;
    private String requestID;
    private String sessionID;
    private String ipPortParentNode = "";
    private String ipPortCurrentNode = "";
    private String requestContent;
    private long startTime;
    private long endTime;
    private long duration;
    private String errorCode;
    private String errorDescription;
    private String transactionStatus;
    private String actionName;
    private String username;
    private String account;
    private String threadName;
    private String sourceClass = "";
    private String sourceLine = "";
    private String sourceMethod = "";
    private String serviceProvider = "";
    private String transactionID;
    private String clientRequestID;
    private String clientIP;
    private String responseContent;
    private String transactionType;
    private String system;
    private String actionType;
    private String dataType = "";
    private String numRecord = "";
    private String correlationID;
    private String imeiTel;
    private String imei;
    private String appVersion;
    private String spanId;
    private String typeOS;
    private String osVersion;
    private String userAgent;
    private String client;
}

