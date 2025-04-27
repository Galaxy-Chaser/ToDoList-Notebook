package org.example.todolistandnotebook.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.service.UsersServiceImpl;
import org.example.todolistandnotebook.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersServiceImpl usersServiceImpl;

    //注册
    @PostMapping("/register")
    public CommonResponse<String> insert(@RequestBody User user , @RequestParam String verification) {

        //验证verification
        if(!usersServiceImpl.CheckVerificationCode(user , verification)) {
            log.info("验证码不匹配");
            return CommonResponse.error();
        }

        //注册
        usersServiceImpl.InsertUsers(user);

        return CommonResponse.success("注册成功");
    }

    //获取验证码
    @PostMapping("/getVerificationCode")
    public CommonResponse<String> register(@RequestBody User user) {
        usersServiceImpl.CreateVerificationCode(user);
        return CommonResponse.success("已发送验证码到邮箱");
    }

    //登录
    @PostMapping("/login")
    public CommonResponse<String> login(@RequestBody User user) {

        //校验用户账号密码是否匹配
        User newUser = usersServiceImpl.LoginUsers(user);
        if(newUser == null) {
            return CommonResponse.error();
        }

        //生成jwt令牌并返回
        Map<String , Object> map = new HashMap<>();
        map.put("id", newUser.getId());
        map.put("email", newUser.getEmail());
        map.put("username", newUser.getUsername());
        JwtUtils jwtUtils = new JwtUtils();
        return CommonResponse.success(jwtUtils.GenJwt(map));
    }

    //删除
    @DeleteMapping
    public CommonResponse<String> delete(@RequestParam Integer id) {
        if(id == null) {
            return CommonResponse.error("不存在该用户");
        }
        usersServiceImpl.DeleteUsers(id);
        return CommonResponse.success("成功删除用户");
    }

    //修改密码
    @PutMapping
    public CommonResponse<String> updatePassword(@RequestBody User user  , @RequestParam String verification) {
        //验证verification
        if(!usersServiceImpl.CheckVerificationCode(user , verification)) {
            log.info("验证码不匹配");
            return CommonResponse.error("验证码不匹配");
        }
        usersServiceImpl.UpdateUsers(user);
        return CommonResponse.success("密码修改成功");
    }

}
