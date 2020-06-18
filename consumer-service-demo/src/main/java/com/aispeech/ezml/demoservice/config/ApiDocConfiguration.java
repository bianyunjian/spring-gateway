package com.aispeech.ezml.demoservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@OpenAPIDefinition(
        info = @Info(
                title = "gateway API",
                version = "v1.0.1",
                description = "gateway服务"
        )
)
@Configuration
public class ApiDocConfiguration {
}
