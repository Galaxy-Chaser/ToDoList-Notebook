package org.example.todolistandnotebook.backend.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class NoteFile {
    private Long id;
    private String originalName;  // 原始文件名
    private String storedName;    // 存储的唯一文件名
    private String filePath;      // 文件存储路径
    private String fileType;     // 文件类型
    private Long size;           // 文件大小（字节）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;

    private Long noteId;         // 关联的笔记ID
    private Integer userId;      // 文件归属用户

    private String fileHash;
}