package org.example.todolistandnotebook.backend.controller;

import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.ListOfTodos;
import org.example.todolistandnotebook.backend.pojo.Todo;
import org.example.todolistandnotebook.backend.service.TodosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodosController {

    @Autowired
    private TodosService service;

    //增
    @PostMapping
    public CommonResponse<Todo> addTodo(@RequestBody Todo todo) {
        Todo newTodo = service.InsertTodos(todo);
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
    @GetMapping("/getAll")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetAllTodos(pageNum, pageSize));
    }

    //根据标题内容查询
    @GetMapping("/getByTitle")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam String title , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByTitle(title , pageNum , pageSize));
    }

    //根据截止日期查询
    @GetMapping("/getByDueDate")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Date startDate, @RequestParam Date endDate , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByDueDate(startDate , endDate , pageNum, pageSize));
    }

    //根据状态查询
    @GetMapping("/getByStatus")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Integer status , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1 || status < 0 || status > 2)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByStatus(status , pageNum, pageSize));
    }

    //根据截止日期和状态查询
    @GetMapping("/getByDueDateAndStatus")
    public CommonResponse<ListOfTodos> getTodos(@RequestParam Date startDate, @RequestParam Date endDate , @RequestParam Integer status , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1 || status < 0 || status > 2)
            return CommonResponse.error();
        return CommonResponse.success(service.GetTodosByDueDateAndStatus(startDate , endDate , status , pageNum, pageSize));
    }


    //改
    @PutMapping
    public CommonResponse<Todo> updateTodo(@RequestBody Todo todo) {
        Todo updatedTodo = service.UpdateTodos(todo);
        return CommonResponse.success(updatedTodo);
    }

}
