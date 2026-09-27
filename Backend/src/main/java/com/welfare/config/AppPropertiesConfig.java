package com.welfare.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        OpenApiProperties.class,
        OpenAiProperties.class,
        CorsProperties.class
})
public class AppPropertiesConfig {
}
