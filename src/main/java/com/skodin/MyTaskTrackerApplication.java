package com.skodin;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyTaskTrackerApplication {
    // TODO: 020 добавить изменение order в методе taskStateController.updateProject
    // TODO: 020 добавить ленивые загрузки и тд
    // TODO: 020 прописать сценарии для фронта и адаптировать все под них
    // TODO: 022 удалить isDone из тасок т.к. оно не надо
//    что если по отдавать по определенному адресу только запрашиваемую сущность без вложенных объектов
    public static void main(String[] args) {
        SpringApplication.run(MyTaskTrackerApplication.class, args);
    }

}
