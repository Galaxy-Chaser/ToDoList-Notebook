package org.example.todolistandnotebook.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.todolistandnotebook.backend.pojo.TodoReminder;

import java.util.List;

/**
 * @className: TodoRemindersMapper
 * @packageName: toDoListAndNoteBook
 * @description: 用于处理提醒事项的Mapper接口
 **/
@Mapper
public interface TodoRemindersMapper {

    @Select("SELECT * FROM todoReminder WHERE userId = #{userId}")
    List<TodoReminder> getByUserId(int userId);

    @Select("SELECT * FROM todoReminder WHERE id = #{id}")
    TodoReminder getById(int id);

    @Select("SELECT * FROM todoReminder WHERE isSend = 0 AND userId = #{userId}")
    List<TodoReminder> getUnsentByUserId(int userId);

    @Options(keyProperty = "id" , useGeneratedKeys = true)
    @Insert("INSERT INTO todoReminder(message, userId , todoId , isSend) VALUES(#{message}, #{userId} , #{todoId} ,  #{isSend})")
    void insert(TodoReminder todoReminder);

    @Delete("DELETE FROM todoReminder WHERE id = #{id}")
    void delete(int id);

    @Delete("DELETE FROM todoReminder WHERE todoId = #{todoId}")
    void deleteByTodoId(Long todoId);

    @Update("UPDATE todoReminder SET isSend = #{isSend} WHERE id = #{id}")
    void updateIsSend(TodoReminder todoReminder);

}
