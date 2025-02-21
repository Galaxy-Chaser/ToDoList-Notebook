package org.example.todolistandnotebook.backend.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class Todo {
    private Long id;
    private String title;
    private String description;
    private Date dueDate;
    private Integer status;//0为未完成 ， 1为已完成
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatedAt;
}
