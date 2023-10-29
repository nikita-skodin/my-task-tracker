package com.skodin;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyTaskTrackerApplication {
    // TODO: 020 добавить ленивые загрузки и тд
    public static void main(String[] args) {
        SpringApplication.run(MyTaskTrackerApplication.class, args);
    }
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Task Tracker API")
                        .description("Spring Boot Application")
                        .version("1.0"));
    }
}
