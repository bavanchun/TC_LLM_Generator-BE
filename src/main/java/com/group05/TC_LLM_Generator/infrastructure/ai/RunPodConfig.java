package com.group05.TC_LLM_Generator.infrastructure.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RunPod vLLM serverless endpoint.
 */
@Configuration
@ConfigurationProperties(prefix = "runpod")
@Getter
@Setter
public class RunPodConfig {

    private String endpointUrl;
    private String apiKey;
    private String model;

    @Bean("runpodRestTemplate")
    public RestTemplate runpodRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);    // 30s connect
        factory.setReadTimeout(120_000);      // 120s read (cold start can be slow)
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
