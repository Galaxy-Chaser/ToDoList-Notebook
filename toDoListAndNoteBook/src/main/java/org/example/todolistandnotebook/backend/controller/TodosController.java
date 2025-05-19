package org.example.todolistandnotebook.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.ListOfTodos;
import org.example.todolistandnotebook.backend.pojo.Todo;
import org.example.todolistandnotebook.backend.pojo.TodoReminder;
import org.example.todolistandnotebook.backend.service.TodosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todos")
public class TodosController {

    @Autowired
    private TodosServiceImpl service;

    //增
    @PostMapping
    public CommonResponse<Todo> addTodo(@RequestBody Todo todo , @RequestHeader("Authorization") String jwt) {
        Todo newTodo = service.InsertTodos(todo, jwt);
        return CommonResponse.success(newTodo);
    }

    //删
    @DeleteMapping
    public CommonResponse deleteTodo(@RequestParam Long id) {
        if(id == null)
            return CommonResponse.error();
        service.DeleteTodos(id);
        return CommonResponse.success();
    }

    //查
    //查所有代表事项
    @GetMapping
    public CommonResponse<ListOfTodos> getTodos(@RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize , @RequestHeader("Authorization") String jwt) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetAllTodos(pageNum, pageSize, jwt));
    }

    //根据标题内容查询
    @GetMapping(params = "title")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam String title , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize , @RequestHeader("Authorization") String jwt) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByTitle(title , pageNum , pageSize, jwt));
    }

    //根据截止日期查询
    @GetMapping(params = {"startDate" , "endDate"})
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Date startDate, @RequestParam Date endDate , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize , @RequestHeader("Authorization") String jwt) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByDueDate(startDate , endDate , pageNum, pageSize, jwt));
    }

    //根据状态查询
    @GetMapping(params = "status")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Integer status , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize , @RequestHeader("Authorization") String jwt) {
        if(pageNum < 1 || status < 0 || status > 2)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByStatus(status , pageNum, pageSize, jwt));
    }

    //根据截止日期和状态查询
    @GetMapping(params = {"startDate" , "endDate" , "status"})
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Date startDate, @RequestParam Date endDate , @RequestParam Integer status , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize , @RequestHeader("Authorization") String jwt) {
        if(pageNum < 1 || status < 0 || status > 2)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByDueDateAndStatus(startDate , endDate , status , pageNum, pageSize, jwt));
    }

    @GetMapping("/todoReminder")
    public CommonResponse<List<TodoReminder>> getTodo(@RequestHeader("Authorization") String jwt) {
        return CommonResponse.success(service.getTodoReminders(jwt));
    }

    //改
    @PutMapping
    public CommonResponse<Todo> updateTodo(@RequestBody Todo todo) {
        Todo updatedTodo = service.UpdateTodos(todo);
        return CommonResponse.success(updatedTodo);
    }

    @DeleteMapping("/todoReminder")
    public CommonResponse deleteTodoReminder(
            @RequestParam(required = false) Integer ReminderId,
            @RequestParam(required = false) List<Integer> ReminderIds) {

        // 确保至少有一个参数存在
        if (ReminderId == null && ReminderIds == null) {
            return CommonResponse.error("缺少参数");
        }

        if (ReminderId != null) {
            service.deleteTodoReminder(ReminderId);
        } else {
            for (Integer id : ReminderIds) {
                service.deleteTodoReminder(id);
            }
        }

        return CommonResponse.success();
    }

}
