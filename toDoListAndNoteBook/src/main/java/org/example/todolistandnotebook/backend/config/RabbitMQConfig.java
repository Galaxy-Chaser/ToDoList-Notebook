package org.example.todolistandnotebook.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // 延迟交换机（需安装 rabbitmq_delayed_message_exchange 插件）
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
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
        return new Queue("todo.reminder.queue");
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
