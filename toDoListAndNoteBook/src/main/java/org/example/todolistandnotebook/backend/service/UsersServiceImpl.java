package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.mapper.UsersMapper;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.utils.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Random;

@Service
public class UsersServiceImpl implements org.example.todolistandnotebook.backend.service.iService.UsersService {

    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private NotesServiceImpl notesServiceImpl;

    @Autowired
    private TodosServiceImpl todosServiceImpl;

    @Autowired
    private EmailUtils emailUtils;

    JedisPool jedisPool = new JedisPool("localhost", 6379);

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
        notesServiceImpl.DeleteNoteByUserId(id);
        todosServiceImpl.DeleteTodosByUserId(id);
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

        //生成验证码
        Random rand = new Random();
        String verification = String.format("%06d", rand.nextInt(1000000));
        String redisKey = user.getEmail() + "VerificationCode";

        //保存验证码到redis
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(redisKey, verification);
            jedis.expire(redisKey, 60 * 5);
        }
        //发送邮件
        emailUtils.sendTxtEmail(user.getEmail() , "验证码：" , verification);
    }

    @Override
    public boolean CheckVerificationCode(User user , String verificationCode) {

        String redisKey = user.getEmail() + "VerificationCode";
        try (Jedis jedis = jedisPool.getResource()) {
            //判断验证码是否存在或过期
            if (!jedis.exists(redisKey)) {
                log.info("not exist");
                return false;
            }
            log.info(jedis.get(redisKey));
            //判断验证码是否正确
            boolean flag = jedis.get(redisKey).equals(verificationCode);
            if(flag)
                jedis.del(redisKey);
            //返回验证结果
            return flag;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
