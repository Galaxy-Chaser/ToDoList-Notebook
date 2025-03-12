package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String email;
    private String username;
    private String password;
}
