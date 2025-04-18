package org.example.todolistandnotebook;

import org.example.todolistandnotebook.backend.util.EmailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.util.Random;

@SpringBootTest
class ToDoListAndNoteBookApplicationTests {

    @Autowired
    private EmailUtil emailUtil;

    @Test
    void contextLoads() {
    }

    @Test
    public void testRedis() {
        Jedis jedis = new Jedis("localhost" , 6379);

        //生成验证码
        Random rand = new Random();
        String verification = String.format("%06d", rand.nextInt(1000000));
        String redisKey = "291204058@qq.com" + "VerificationCode";

        //发送邮件
        emailUtil.sendTxtEmail("291204058@qq.com" , "验证码：" , verification);

        jedis.set(redisKey , verification);
        //设置过期时间5min
        jedis.expire(redisKey, 60 * 5);
        jedis.close();
    }
}
