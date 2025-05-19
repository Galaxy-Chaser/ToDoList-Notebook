package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

/**
 * @className: SignupRequest
 * @packageName: toDoListAndNoteBook
 * @description: 用于接收注册请求的类
 **/
@Data
public class SignupRequest {
    /**用户id**/
    private Integer id;
    /**用户邮箱**/
    private String email;
    /**用户名**/
    private String username;
    /**用户密码**/
    private String password;
    /**验证码**/
    private String verification;
}
