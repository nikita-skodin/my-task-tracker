package com.skodin;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyTaskTrackerApplication {
    // TODO: 020 добавить ленивые загрузки и тд
    // TODO: 020 прописать сценарии для фронта и адаптировать все под них, делаем в новом приложении
    // TODO: 023 разобратся с бд task почему там не виден внешний ключ
    public static void main(String[] args) {
        SpringApplication.run(MyTaskTrackerApplication.class, args);
    }

}
