package org.example.todolistandnotebook.backend.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.sql.Timestamp;

/**
 * @packageName: org.example.todolistandnotebook.backend.pojo
 * @className: NoteFile
 * @description: 笔记的文件信息
 */
@Data
public class NoteFile {
    /**笔记文件的id**/
    private Long id;
    /**原始文件名**/
    private String originalName;
    /**存储的唯一文件名**/
    private String storedName;
    /**文件存储路径**/
    private String filePath;
    /**文件类型**/
    private String fileType;
    /**文件大小（字节）**/
    private Long size;
    /**当前信息创建时间 时间格式：yyyy-MM-dd HH:mm:ss**/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;
    /**关联的笔记ID**/
    private Long noteId;
    /**文件归属用户**/
    private Integer userId;
    /**文件HASH值**/
    private String fileHash;
}