package com.skodin;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyTaskTrackerApplication {
    // TODO: 020 добавить изменение order в методе taskStateController.updateProject
    public static void main(String[] args) {
        SpringApplication.run(MyTaskTrackerApplication.class, args);
    }

}
