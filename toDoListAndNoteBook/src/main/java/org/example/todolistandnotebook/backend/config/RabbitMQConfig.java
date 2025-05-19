package org.example.todolistandnotebook.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @packageName: org.example.todolistandnotebook.backend.config
 * @className: RabbitMQConfig
 * @description: RabbitMQ配置类, 包括延迟交换机、提醒队列、绑定交换机与队列
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        //设置交换机类型
        args.put("x-delayed-type", "direct");
        return new CustomExchange(
                "todo.delayed.exchange",
                "x-delayed-message",
                true,
                false,
                args
        );
    }

    // 提醒队列
    @Bean
    public Queue reminderQueue() {
        return new Queue("todo.reminder.queue" , true);
    }

    // 绑定交换机与队列
    @Bean
    public Binding binding(Queue reminderQueue, CustomExchange delayedExchange) {
        return BindingBuilder
                .bind(reminderQueue)
                .to(delayedExchange)
                .with("todo.reminder.routingKey")
                .noargs();
    }
}
