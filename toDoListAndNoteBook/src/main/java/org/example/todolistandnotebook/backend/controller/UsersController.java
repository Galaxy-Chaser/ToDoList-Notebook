package org.example.todolistandnotebook.backend.controller;

import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.service.UsersService;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    //注册
    @PostMapping
    public CommonResponse<String> insert(@RequestBody User user) {
        User newUser = usersService.InsertUsers(user);
        return CommonResponse.success("注册成功");
    }

    //登录
    @PostMapping("/login")
    public CommonResponse<String> login(@RequestBody User user) {
        User newUser = usersService.LoginUsers(user);
        if(newUser == null) {
            return CommonResponse.error();
        }
        Map<String , Object> map = new HashMap<>();
        map.put("id", newUser.getId());
        map.put("email", newUser.getEmail());
        map.put("username", newUser.getUsername());
        JwtUtil jwtUtil = new JwtUtil();
        return CommonResponse.success(jwtUtil.GenJwt(map));
    }

    //删除
    @DeleteMapping
    public CommonResponse<String> delete(@RequestParam Integer id) {
        if(id == null) {
            return CommonResponse.error();
        }
        usersService.DeleteUsers(id);
        return CommonResponse.success("成功删除用户");
    }

    //修改密码
    @PutMapping
    public CommonResponse<String> updatePassword(@RequestBody User user) {
        usersService.UpdateUsers(user);
        return CommonResponse.success("密码修改成功");
    }

}
