package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.mapper.TodoRemindersMapper;
import org.example.todolistandnotebook.backend.mapper.TodosMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfTodos;
import org.example.todolistandnotebook.backend.pojo.Todo;
import org.example.todolistandnotebook.backend.pojo.TodoReminder;
import org.example.todolistandnotebook.backend.utils.JwtUtils;
import org.example.todolistandnotebook.backend.utils.WebsocketUserManageUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TodosServiceImpl implements org.example.todolistandnotebook.backend.service.iService.TodosService {

    @Autowired
    private TodosMapper todosMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private TodoRemindersMapper todoRemindersMapper;

    @Autowired
    private WebsocketUserManageUtils websocketUserManageUtils;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public Todo InsertTodos(Todo todo , String jwt) {
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        todo.setStatus(0);
        todo.setUserId(userId);
        todo.setReminded(0);
        todosMapper.insert(todo);
        Long id = todo.getId();
        Todo retTodo = todosMapper.getById(id);
        //将消息发送到rabbitmq，用于在截止时间前1天提醒
        ScheduleReminder(retTodo);
        return retTodo;
    }

    @Override
    public Todo UpdateTodos(Todo todo){
        Long id = todo.getId();
        todosMapper.update(todo);
        Todo retTodo = todosMapper.getById(id);
        //将消息发送到rabbitmq，用于在截止时间前1天提醒
        ScheduleReminder(retTodo);
        return retTodo;
    }

    @Override
    @Transactional
    public Void DeleteTodos(Long id) {
        todoRemindersMapper.deleteByTodoId(id);
        todosMapper.delete(id);
        return null;
    }

    @Override
    public ListOfTodos GetTodosByDueDateAndStatus(Date start, Date end, Integer status , Integer pageNum , Integer pageSize , String jwt) {
        //获取用户id
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(start , end , status , userId);
        //分页
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByDueDate(Date start, Date end , Integer pageNum , Integer pageSize , String jwt) {
        //获取用户id
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(start , end , null , userId);
        //分页
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByStatus(Integer status , Integer pageNum , Integer pageSize , String jwt) {
        //获取用户id
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(null , null , status , userId);
        //分页
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByTitle(String title, Integer pageNum , Integer pageSize , String jwt) {
        //获取用户id
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByTitle(title , userId);
        //分页
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetAllTodos(Integer pageNum , Integer pageSize , String jwt) {
        //获取用户id
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getAll(userId);
        //分页
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public void DeleteTodosByUserId(Integer id) {
        List<Long> todos = todosMapper.getByUserId(id);
        if(todos != null && !todos.isEmpty()) {
            for(Long todo : todos) {
                todosMapper.delete(todo);
            }
        }
        return;
    }

    @Override
    public void ScheduleReminder(Todo todo) {
        //计算延迟时间
        LocalDateTime dueDate = todo.getDueDate().toLocalDateTime();
        LocalDateTime reminderTime = dueDate.minusDays(1); // 截止时间前1天

        long delay = Duration.between(LocalDateTime.now(), reminderTime).toMillis();
        log.info(String.valueOf(delay));
        log.info("正在将消息发送到rabbitmq");

        //delay小于0说明ddl在一天之内，无需发送提醒
        if (delay > 0) {
            //发送延迟消息
            rabbitTemplate.convertAndSend(
                    "todo.delayed.exchange",
                    "todo.reminder.routingKey",
                    todo.getId(),
                    message -> {
                        message.getMessageProperties().setHeader("x-delay" , delay);
                        return message;
                    }
            );
            log.info("已发送延迟提醒消息，任务ID: {}", todo.getId());
        }
    }

    @Override
    @RabbitListener(queues = "todo.reminder.queue")
    public void Reminder(Long todoId) {
        log.info("Reminder information : todoId {}", todoId);
        Todo todo = todosMapper.getById(todoId);

        //找不到待办事项直接返回，说明该todo可能被删除了
        if (todo == null)
            return;

        // reminded == 1 , 说明已经提醒过了
        if (todo.getReminded() == 1) {
            log.info("Todo ID: {} already reminded.", todoId);
            return;
        }

        //待办事项ddl被延后，还没到通知时间
        LocalDateTime dueDate = todo.getDueDate().toLocalDateTime();
        LocalDateTime reminderTime = dueDate.minusDays(1); // 截止时间前1天
        long delay = Duration.between(LocalDateTime.now(), reminderTime).toMillis();
        //给10s误差
        if (delay > 10000) {
            log.info("Todo ID: {} reminder time not yet reached (delay: {} ms). Rescheduling check potentially needed if logic requires it.", todoId, delay);
            return;
        }

        // 通过 WebSocket 发送提醒
        String message = String.format(
                "任务 [%s] 将于 %s 截止，请及时处理！",
                todo.getTitle(),
                todo.getDueDate().toString().substring(0, 16)
        );
        log.info(message);
        if (websocketUserManageUtils.isUserOnline(todo.getUserId())) {
            // 1) 写库，标记 sent=1 并拿到主键
            TodoReminder rec = new TodoReminder();
            rec.setMessage(message);
            rec.setUserId(todo.getUserId());
            rec.setTodoId(todo.getId());
            rec.setIsSend(1);
            todoRemindersMapper.insert(rec);
            int reminderId = rec.getId();  // MyBatis 会回填
            log.info("Todo ID: {} reminder sent, id: {}.", todoId, reminderId);

            // 2) 推送带 id + message 的 JSON
            Map<String ,Object> payload = Map.of(
                    "id", reminderId,
                    "message", message
            );
            messagingTemplate.convertAndSend(
                    "/topic/reminders." + todo.getUserId(),
                    payload
            );
        } else {
            // 离线分支保持原样
            TodoReminder rec = new TodoReminder();
            rec.setMessage(message);
            rec.setUserId(todo.getUserId());
            rec.setTodoId(todo.getId());
            rec.setIsSend(0);
            todoRemindersMapper.insert(rec);
        }
        // 更新提醒状态
        todo.setReminded(1);
        todosMapper.update(todo);
    }

    @Override
    public List<TodoReminder> getTodoReminders(String jwt) {
        int userId = jwtUtils.getIdFromJwt(jwt);
        return todoRemindersMapper.getUnsentByUserId(userId);
    }

    @Override
    public void deleteTodoReminder(int id) {
        todoRemindersMapper.delete(id);
    }
}
