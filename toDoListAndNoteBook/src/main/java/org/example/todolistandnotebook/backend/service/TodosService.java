package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.mapper.TodosMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfTodos;
import org.example.todolistandnotebook.backend.pojo.Todo;
import org.example.todolistandnotebook.backend.service.IService.TodosIService;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Slf4j
@Service
public class TodosService implements TodosIService {

    @Autowired
    private TodosMapper todosMapper;

    private JwtUtil jwtUtil;

    @Transactional
    @Override
    public Todo InsertTodos(Todo todo , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        todo.setStatus(0);
        todo.setUserId(userId);
        todosMapper.insert(todo);
        Long id = todo.getId();
        return todosMapper.getById(id);
    }

    @Override
    public Todo UpdateTodos(Todo todo){
        Long id = todo.getId();
        todosMapper.update(todo);
        return todosMapper.getById(id);
    }

    @Override
    public Void DeleteTodos(Long id) {
        todosMapper.delete(id);
        return null;
    }

    @Override
    public ListOfTodos GetTodosByDueDateAndStatus(Date start, Date end, Integer status , Integer pageNum , Integer pageSize , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(start , end , status , userId);
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByDueDate(Date start, Date end , Integer pageNum , Integer pageSize , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(start , end , null , userId);
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByStatus(Integer status , Integer pageNum , Integer pageSize , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByDueDateOrStatus(null , null , status , userId);
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetTodosByTitle(String title, Integer pageNum , Integer pageSize , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getByTitle(title , userId);
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfTodos GetAllTodos(Integer pageNum , Integer pageSize , String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum,pageSize);
        List<Todo> todos = todosMapper.getAll(userId);
        PageInfo<Todo> pageInfos = new PageInfo<>(todos);
        return new ListOfTodos(pageInfos.getList() , pageInfos.getTotal());
    }
}
