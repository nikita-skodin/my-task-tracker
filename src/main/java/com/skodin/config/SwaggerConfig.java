package com.skodin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class SwaggerConfig {
    @Bean
    @SneakyThrows
    public OpenAPI myOpenAPI() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("openapi/swagger-config.yaml");
        return objectMapper.readValue(inputStream, OpenAPI.class);
    }
}
