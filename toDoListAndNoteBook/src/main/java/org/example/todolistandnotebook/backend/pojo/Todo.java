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
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp dueDate;
    private Integer status;//0为未完成 ， 1为已完成
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;
    private Integer userId;
    private Integer reminded;//0为未提醒，1为已提醒
}
