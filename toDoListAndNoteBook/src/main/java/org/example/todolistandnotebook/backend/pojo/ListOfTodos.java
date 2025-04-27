package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

import java.util.List;

/**
 * @className: ListOfTodos
 * @packageName: org.example.todolistandnotebook.backend.pojo
 * @description: 用于以列表的形式返回待办事项及待办事项总数
 */
@Data
public class ListOfTodos {
    /**待办事项列表**/
    private List<Todo> todos;
    /**待办事项总数**/
    private Long total;

    /**构造方法**/
    public ListOfTodos(List<Todo> list, long total) {
        this.todos = list;
        this.total = total;
    }
}
