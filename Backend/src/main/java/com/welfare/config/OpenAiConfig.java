package com.welfare.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAIClient openAIClient(OpenAiProperties properties) {
        OpenAIOkHttpClient.Builder builder = OpenAIOkHttpClient.builder()
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .maxRetries(properties.getMaxRetries());

        if (StringUtils.hasText(properties.getApiKey())) {
            builder.apiKey(properties.getApiKey());
        } else {
            builder.fromEnv();
        }

        return builder.build();
    }
}
