package org.example.todolistandnotebook.backend.controller;

import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping
    public CommonResponse<String> login(@RequestBody User user) {
        if(!user.getUsername().equals("admin") || !user.getPassword().equals("123456")) {
            return CommonResponse.error();
        }
        Map<String , Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        JwtUtil jwtUtil = new JwtUtil();
        return CommonResponse.success(jwtUtil.GenJwt(map));
    }
}
