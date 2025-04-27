package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

@Data
public class User {
    /**用户id**/
    private Integer id;
    /**用户邮箱**/
    private String email;
    /**用户名**/
    private String username;
    /**用户密码**/
    private String password;
}
