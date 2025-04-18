package org.example.todolistandnotebook.backend.service.IService;

import org.example.todolistandnotebook.backend.pojo.ListOfTodos;
import org.example.todolistandnotebook.backend.pojo.Todo;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public interface TodosIService {

    /**
     *新增待办事项
     *
     * @param todo 待办事项
     * @param jwt 待办事项
     */
    Todo InsertTodos(Todo todo , String jwt);

    /**
     *修改待办事项
     *
     * @param todo 待办事项
     * @return Todo 修改后的待办事项
     */
    Todo UpdateTodos(Todo todo);

    /**
     *删除待办事项
     *
     * @param id 待办事项id
     */
    Void DeleteTodos(Long id);

    /**
     * 根据截止时间和状态
     * 查询待办事项
     *
     * @param start 范围的开始
     * @param end 范围的结束
     * @param status 待办事项的状态
     * @param jwt jwt令牌
     * @return ListOfTodos 待办事项列表
     */
    ListOfTodos GetTodosByDueDateAndStatus(Date start, Date end , Integer status , Integer pageNum , Integer pageSize , String jwt);

    /**
     * 根据截止时间
     * 查询待办事项
     *
     * @param start 范围的开始
     * @param end 范围的结束
     * @param jwt jwt令牌
     * @return ListOfTodos 待办事项列表
     */
    ListOfTodos GetTodosByDueDate(Date start, Date end , Integer pageNum , Integer pageSize , String jwt);

    /**
     * 根据状态
     * 查询待办事项
     *
     * @param status 待办事项的状态
     * @param jwt jwt令牌
     * @return ListOfTodos 待办事项列表
     */
    ListOfTodos GetTodosByStatus(Integer status , Integer pageNum , Integer pageSize , String jwt);

    /**
     * 根据标题内容
     * 查询待办事项
     *
     * @param title 待办事项的标题
     * @param jwt jwt令牌
     * @return ListOfTodos 待办事项列表
     */
    ListOfTodos GetTodosByTitle(String title , Integer pageNum , Integer pageSize , String jwt);

    /**
     * 查询所有待办事项
     *@param jwt jwt令牌
     * @return ListOfTodos 待办事项列表
     */
    ListOfTodos GetAllTodos(Integer pageNum , Integer pageSize , String jwt);

    /**
     * 根据用户id删除待办事项
     * @param id 用户id
     */
    void DeleteTodosByUserId(Integer id);
}
