# RunPod Integration - Remaining Files

## ✅ Already Created:
1. `LLMProviderPort.java` - Port interface
2. `ChatMessage.java` - DTO
3. `ChatCompletionRequest.java` - DTO  
4. `ChatCompletionResponse.java` - DTO

---

## 📝 TODO: Add These Files

### 1. Update `pom.xml` - Add dependency:

```xml
<!-- Add inside <dependencies> -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### 2. Update `.env` - Add RunPod config:

```bash
# RunPod LLM Configuration
RUNPOD_API_URL=https://your-pod-id-8000.proxy.runpod.net
RUNPOD_MODEL_NAME=meta-llama/Meta-Llama-3.1-8B-Instruct
RUNPOD_TIMEOUT=60
RUNPOD_MAX_TOKENS=2000
RUNPOD_TEMPERATURE=0.7
```

### 3. Create `application.yml` in `src/main/resources/`:

```yaml
spring:
  application:
    name: TC_LLM_Generator
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev,postgres}

llm:
  provider: runpod
  runpod:
    api-url: ${RUNPOD_API_URL:http://localhost:11434}
    model-name: ${RUNPOD_MODEL_NAME:meta-llama/Meta-Llama-3.1-8B-Instruct}
    timeout: ${RUNPOD_TIMEOUT:60}
    max-tokens: ${RUNPOD_MAX_TOKENS:2000}
    temperature: ${RUNPOD_TEMPERATURE:0.7}
```

### 4. Create Configuration Files:

#### `infrastructure/ai/config/WebClientConfig.java`:

```java
package com.group05.TC_LLM_Generator.infrastructure.ai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${llm.runpod.api-url}")
    private String runpodApiUrl;

    @Value("${llm.runpod.timeout}")
    private int timeoutSeconds;

    @Bean(name = "llmWebClient")
    public WebClient llmWebClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .responseTimeout(Duration.ofSeconds(timeoutSeconds))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
            );

        return WebClient.builder()
            .baseUrl(runpodApiUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
```

#### `infrastructure/ai/adapter/RunPodLLMAdapter.java`:

```java
package com.group05.TC_LLM_Generator.infrastructure.ai.adapter;

import com.group05.TC_LLM_Generator.application.port.out.LLMProviderPort;
import com.group05.TC_LLM_Generator.infrastructure.ai.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RunPodLLMAdapter implements LLMProviderPort {

    private final WebClient webClient;

    @Value("${llm.runpod.model-name}")
    private String modelName;

    @Value("${llm.runpod.max-tokens}")
    private Integer defaultMaxTokens;

    @Value("${llm.runpod.temperature}")
    private Double defaultTemperature;

    public RunPodLLMAdapter(@Qualifier("llmWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<String> generateCompletion(String prompt, Map<String, Object> params) {
        log.info("Generating completion via RunPod. Prompt length: {}", prompt.length());

        ChatMessage userMessage = ChatMessage.builder()
            .role("user")
            .content(prompt)
            .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model(modelName)
            .messages(List.of(userMessage))
            .maxTokens((Integer) params.getOrDefault("maxTokens", defaultMaxTokens))
            .temperature((Double) params.getOrDefault("temperature", defaultTemperature))
            .stream(false)
            .build();

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatCompletionResponse.class)
            .map(response -> {
                String content = response.getChoices().get(0).getMessage().getContent();
                log.info("RunPod response received. Tokens: {}", 
                    response.getUsage().getTotalTokens());
                return content;
            })
            .doOnError(error -> log.error("RunPod API error: ", error));
    }
}
```

### 5. Create Test Controller:

#### `presentation/controller/AITestController.java`:

```java
package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.out.LLMProviderPort;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai/test")
@RequiredArgsConstructor
public class AITestController {

    private final LLMProviderPort llmProvider;

    @PostMapping("/generate")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> testGenerate(
        @RequestBody Map<String, String> request
    ) {
        String prompt = request.getOrDefault("prompt", "Say hello!");

        return llmProvider.generateCompletion(prompt, new HashMap<>())
            .map(response -> {
                Map<String, Object> data = new HashMap<>();
                data.put("prompt", prompt);
                data.put("response", response);
                
                return ResponseEntity.ok(
                    ApiResponse.success("LLM generation successful", data)
                );
            })
            .onErrorResume(error -> {
                log.error("Error generating completion: ", error);
                return Mono.just(
                    ResponseEntity.badRequest().body(
                        ApiResponse.error("LLM generation failed: " + error.getMessage())
                    )
                );
            });
    }
}
```

---

## 🧪 Testing After Setup:

### 1. Start backend:
```bash
cd TC_LLM_Generator-BE
./mvnw spring-boot:run
```

### 2. Test endpoint:
```bash
curl -X POST http://localhost:8080/api/v1/ai/test/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Write a test case for a login form. Be concise."
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "LLM generation successful",
  "data": {
    "prompt": "Write a test case for a login form. Be concise.",
    "response": "Test Case: Valid Login\n\nPreconditions:\n- User has valid credentials\n...",
  },
  "timestamp": "2026-03-19T20:00:00Z"
}
```

---

## Next Steps:

After testing works:
1. Implement UserStoryRefinementService
2. Implement TestCaseGenerationService  
3. Create proper endpoints
4. Frontend integration
