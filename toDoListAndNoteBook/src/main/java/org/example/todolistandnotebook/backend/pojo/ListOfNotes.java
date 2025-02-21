package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ListOfNotes {
    private List<Note> notes;
    private Long total;

    public ListOfNotes(List<Note> list, long total) {
        this.notes = list;
        this.total = total;
    }
}
