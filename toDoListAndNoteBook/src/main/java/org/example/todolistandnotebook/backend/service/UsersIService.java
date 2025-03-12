package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.pojo.Todo;
import org.example.todolistandnotebook.backend.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UsersIService {

    /**
     *新增用户
     *
     * @param user 新增用户
     */
    User InsertUsers(User user);

    /**
     *修改用户密码
     *
     * @param user 修改后的用户信息
     */
    User UpdateUsers(User user);

    /**
     *删除用户
     *
     * @param id 待删除的用户的id
     */
    void DeleteUsers(Integer id);

    /**
     *登录
     *
     * @param user 用户的账号密码
     */
    User LoginUsers(User user);
}
