package com.welfare.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "welfare.open-api")
public class OpenApiProperties {

    private String serviceKey;
    private String listCallTp = "L";
    private String detailServCd = "W001";
    private int pageNo = 1;
    private int numOfRows = 200;
    private int maxPages = 10;
    private int timeoutMs = 30000;
    private String nationalListApiCd = "NAW01";
    private String nationalDetailApiCd = "NAW02";
    private String localListApiCd = "LCW01";
    private String localDetailApiCd = "LCW02";
}
