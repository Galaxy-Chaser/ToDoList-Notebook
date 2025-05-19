package org.example.todolistandnotebook.backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.mapper.TodoRemindersMapper;
import org.example.todolistandnotebook.backend.pojo.TodoReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: WebsocketUserManageUtils
 * @packageName: toDoListAndNoteBook
 * @description: 管理websocket用户的工具类
 **/

@Slf4j
@Component
public class WebsocketUserManageUtils {
    // 存储 userId -> sessionId 映射，判断用户是否在线
    private static final Map<Integer, String> userSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TodoRemindersMapper todoRemindersMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /** 添加用户在线记录 */
    public void addUser(Integer userId, String sessionId) {
        userSessionMap.put(userId, sessionId);
    }

    /** 移除用户在线记录 */
    public void removeUserBySessionId(String sessionId) {
        userSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }

    /** 判断用户是否在线 */
    public boolean isUserOnline(Integer userId) {
        return userSessionMap.containsKey(userId);
    }

    /** 获取用户对应的 sessionId */
    public String getSessionIdByUserId(Integer userId) {
        return userSessionMap.get(userId);
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // 优先从STOMP头部获取token
        String token = accessor.getFirstNativeHeader("token");

        // 如果头部中没有token，尝试从查询参数获取（兼容旧实现）
        if (token == null) {
            Object serverHttpRequestObj = accessor.getSessionAttributes().get("org.springframework.http.server.ServerHttpRequest");
            if (serverHttpRequestObj == null) {
                log.error("无法获取ServerHttpRequest，连接中止");
                return;
            }
            String uri = serverHttpRequestObj.toString();
            token = UriComponentsBuilder.fromUriString(uri).build().getQueryParams().getFirst("token");
        }

        if (token == null) {
            log.error("WebSocket连接缺少token参数");
            return;
        }

        log.info("websocket连接，token：{}", token);
        Integer userId = jwtUtils.getIdFromJwt(token);
        String sessionId = accessor.getSessionId();

        if (userId != null) {
            addUser(userId, sessionId);

            // 查询该用户未发送的提醒（is_send = 0）
            List<TodoReminder> reminders = todoRemindersMapper.getUnsentByUserId(userId);
            for (TodoReminder reminder : reminders) {
                messagingTemplate.convertAndSend(
                        "/topic/reminders." + userId,
                        reminder.getMessage()
                );

                // 设置为已发送
                reminder.setIsSend(1);
                todoRemindersMapper.updateIsSend(reminder);
            }
        }
    }

    /** 监听断开事件，清除用户在线状态 */
    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        removeUserBySessionId(sessionId);
        log.info("websocket断开，sessionId：{}", sessionId);
    }

}
