package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.mapper.UsersMapper;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.service.IService.UsersIService;
import org.example.todolistandnotebook.backend.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Random;

@Service
public class UsersService implements UsersIService {

    private static final Logger log = LoggerFactory.getLogger(UsersService.class);
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private NotesService notesService;

    @Autowired
    private TodosService todosService;

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public User InsertUsers(User user) {
        usersMapper.insertUser(user);
        User newUser = usersMapper.getUserById(user.getId());
        return newUser;
    }

    @Override
    public User UpdateUsers(User user) {
        usersMapper.updateUser(user);
        User UpdatedUser = usersMapper.getUserById(user.getId());
        return UpdatedUser;
    }

    @Override
    public void DeleteUsers(Integer id) {
        notesService.DeleteNoteByUserId(id);
        todosService.DeleteTodosByUserId(id);
        usersMapper.deleteUser(id);
    }

    @Override
    public User LoginUsers(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        User tempUser = usersMapper.getUserByUsernameAndPassword(username, password);
        return tempUser;
    }

    @Transactional
    @Override
    public void CreateVerificationCode(User user) {

        Jedis jedis = new Jedis("localhost" , 6379);

        //生成验证码
        Random rand = new Random();
        String verification = String.format("%06d", rand.nextInt(1000000));
        String redisKey = user.getEmail() + "VerificationCode";

        //发送邮件
        emailUtil.sendTxtEmail(user.getEmail() , "验证码：" , verification);

        jedis.set(redisKey , verification);
        //设置过期时间5min
        jedis.expire(redisKey, 60 * 5);
        jedis.close();
    }

    @Override
    public boolean CheckVerificationCode(User user , String verificationCode) {
        Jedis jedis = new Jedis("localhost" , 6379);
        String redisKey = user.getEmail() + "VerificationCode";
        if(!jedis.exists(redisKey)) {
            log.info("not exist");
            return false;
        }
        log.info(jedis.get(redisKey));
        boolean flag = jedis.get(redisKey).equals(verificationCode);
        jedis.close();
        return flag;
    }
}
