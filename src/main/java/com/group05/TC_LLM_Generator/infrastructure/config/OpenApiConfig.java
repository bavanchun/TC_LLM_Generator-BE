package com.group05.TC_LLM_Generator.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Accessible at: /swagger-ui.html or /swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT access token here")))
                .info(new Info()
                        .title("TC LLM Generator API")
                        .version("1.0.0")
                        .description("""
                                RESTful API for Test Case LLM Generator application.

                                ## Features
                                - **Users Management**: Create, read, update, and delete users
                                - **Workspaces Management**: Organize projects in workspaces
                                - **Projects Management**: Manage test case generation projects
                                - **Test Plans Management**: Create and manage test plans
                                - **Test Cases Management**: Generate and manage test cases

                                ## Authentication
                                Use the **Authorize** button (lock icon) and paste your JWT access token to authenticate requests.

                                ## API Endpoints
                                All endpoints follow REST conventions and return JSON responses with the following structure:
                                ```json
                                {
                                  "success": true,
                                  "message": "Operation completed successfully",
                                  "data": { ... },
                                  "timestamp": "2026-02-05T09:00:00.000Z"
                                }
                                ```
                                """)
                        .contact(new Contact()
                                .name("TC LLM Generator Team")
                                .email("support@tclllm.com")
                                .url("https://github.com/QTri909/TC_LLM_Generator-BE"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.tclllm.com")
                                .description("Production Server (Coming Soon)")
                ));
    }
}
