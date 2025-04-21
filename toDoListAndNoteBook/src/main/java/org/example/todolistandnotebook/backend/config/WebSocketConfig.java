package org.example.todolistandnotebook.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 定义 WebSocket 端点，前端通过此地址连接
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:63342" , "http://localhost:8088/http://localhost:8088/toDoListAndNoteBook") // 允许跨域
                .withSockJS(); // 兼容不支持 WebSocket 的浏览器
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置消息代理前缀（前端订阅地址的前缀）
        registry.enableSimpleBroker("/topic", "/queue");
        // 设置应用前缀（前端发送消息的目标地址前缀）
        registry.setApplicationDestinationPrefixes("/app");
    }
}