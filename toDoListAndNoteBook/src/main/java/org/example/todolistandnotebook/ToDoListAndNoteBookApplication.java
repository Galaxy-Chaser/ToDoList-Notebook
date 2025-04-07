package org.example.todolistandnotebook;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@ServletComponentScan
@EnableFileStorage
@SpringBootApplication
public class ToDoListAndNoteBookApplication extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ToDoListAndNoteBookApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ToDoListAndNoteBookApplication.class, args);
    }

}
