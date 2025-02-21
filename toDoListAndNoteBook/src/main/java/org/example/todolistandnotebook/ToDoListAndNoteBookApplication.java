package org.example.todolistandnotebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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
