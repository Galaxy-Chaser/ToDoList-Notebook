package org.example.todolistandnotebook.backend.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @packageName: org.example.todolistandnotebook.backend.pojo
 * @className: Note
 * @description: 笔记信息
 */
@Data
public class Note {
    /**笔记id**/
    private Long id;
    /**笔记标题**/
    private String title;
    /**笔记内容**/
    private String content;
    /**创建时间 ， 格式 ："yyyy-MM-dd HH:mm:ss"**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Timestamp createdAt;
    /**修改时间 ， 格式 ："yyyy-MM-dd HH:mm:ss"**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Timestamp updatedAt;
    /**用户id**/
    private Integer userId;
    /**笔记相关文件**/
    private List<NoteFile> attachedFiles;
}
