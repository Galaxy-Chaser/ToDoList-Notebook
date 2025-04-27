package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

import java.util.List;

/**
 * @className: ListOfNotes
 * @packageName: org.example.todolistandnotebook.backend.pojo
 * @description: 用于以列表的形式返回笔记及笔记总数
 */
@Data
public class ListOfNotes {
    /**笔记列表**/
    private List<Note> notes;
    /**列表笔记总数**/
    private Long total;

    /**构造方法**/
    public ListOfNotes(List<Note> list, long total) {
        this.notes = list;
        this.total = total;
    }
}
