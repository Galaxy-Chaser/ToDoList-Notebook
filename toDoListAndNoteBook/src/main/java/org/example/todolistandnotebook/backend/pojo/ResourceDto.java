package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

/**
 * @className: ResourceDto
 * @packageName: toDoListAndNoteBook
 * @description: 用于文件资源在Controller层与Service层之间的数据传输
 **/
@Data
public class ResourceDto {
    /**文件的媒体类型**/
    MediaType mediaType;
    /**资源对象**/
    Resource resource;
    /**响应标头**/
    String contentDisposition;
}
