package org.example.todolistandnotebook.backend.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Todo {
    /**待办事项id**/
    private Long id;
    /**待办事项标题**/
    private String title;
    /**待办事项描述**/
    private String description;
    /**待办事项截止时间 ， 格式：yyyy-MM-dd HH:mm:ss**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp dueDate;
    /**待办事项状态 0为未完成 ， 1为已完成**/
    private Integer status;
    /**待办事项创建时间 ， 格式：yyyy-MM-dd HH:mm:ss**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    /**待办事项修改时间 ， 格式：yyyy-MM-dd HH:mm:ss**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;
    /**待办事项用户id**/
    private Integer userId;
    /**是否对用户进行提醒 0为未提醒，1为已提醒**/
    private Integer reminded;
}
