package org.example.todolistandnotebook.backend.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @packageName: org.example.todolistandnotebook.backend.utils
 * @className: EmailUtils
 * @description: 邮件工具类,用于设置邮件信息并发送
 */
@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendTxtEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件信息
        // 发送者邮件账号
        message.setFrom(senderEmail);
        // 接收者邮件账号
        message.setTo(to);
        // 邮件主题
        message.setSubject(subject);
        // 邮件内容
        message.setText(body);
        javaMailSender.send(message);
    }
}
