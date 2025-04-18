package org.example.todolistandnotebook.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.service.UsersService;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    //注册
    @PostMapping("/register")
    public CommonResponse<String> insert(@RequestBody User user , @RequestParam String verification) {
        if(!usersService.CheckVerificationCode(user , verification)) {
            log.info("验证码不匹配");
            return CommonResponse.error();
        }
        User newUser = usersService.InsertUsers(user);
        return CommonResponse.success("注册成功");
    }

    //获取验证码
    @PostMapping("/getVerificationCode")
    public CommonResponse<String> register(@RequestBody User user) {
        usersService.CreateVerificationCode(user);
        return CommonResponse.success("已发送验证码到邮箱");
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
            return CommonResponse.error("不存在该用户");
        }
        usersService.DeleteUsers(id);
        return CommonResponse.success("成功删除用户");
    }

    //修改密码
    @PutMapping
    public CommonResponse<String> updatePassword(@RequestBody User user  , @RequestParam String verification) {
        if(!usersService.CheckVerificationCode(user , verification)) {
            log.info("验证码不匹配");
            return CommonResponse.error("验证码不匹配");
        }
        usersService.UpdateUsers(user);
        return CommonResponse.success("密码修改成功");
    }

}
