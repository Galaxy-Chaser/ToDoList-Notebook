package org.example.todolistandnotebook.backend.service.iService;

import org.example.todolistandnotebook.backend.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {

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

    /**
     * 利用用户信息生成验证码并发送至邮件
     * @param user 用户信息
     */
    void CreateVerificationCode(User user);

    /**
     * 判断用户验证码是否输入正确
     * @param user 用户信息
     * @param VerificationCode 验证码
     * @return 是否通过判断
     */
    boolean CheckVerificationCode(User user , String VerificationCode);

}
