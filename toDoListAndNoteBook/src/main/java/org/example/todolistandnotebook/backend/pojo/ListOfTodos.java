package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ListOfTodos {
    private List<Todo> todos;
    private Long total;

    public ListOfTodos(List<Todo> list, long total) {
        this.todos = list;
        this.total = total;
    }
}
