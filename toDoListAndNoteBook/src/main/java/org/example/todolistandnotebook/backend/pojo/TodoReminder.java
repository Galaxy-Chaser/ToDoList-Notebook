package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

/**
 * @className: TodoReminder
 * @packageName: toDoListAndNoteBook
 * @description: 用于记录待办事项提醒，等用户上线后再发送通知
 **/

@Data
public class TodoReminder {
    /**待办事项提醒的id**/
    private int id;
    /**提醒的消息**/
    private String message;
    /**提醒的待办事项id**/
    private Long todoId;
    /**提醒的用户id**/
    private Integer userId;
    /**是否发送 0-未发送 1-已发送**/
    private int isSend;
}
