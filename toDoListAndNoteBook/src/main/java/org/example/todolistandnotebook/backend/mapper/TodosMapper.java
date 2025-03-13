package org.example.todolistandnotebook.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.todolistandnotebook.backend.pojo.Todo;

import java.sql.Date;
import java.util.List;

@Mapper
public interface TodosMapper {

    //插入新todo
    @Options(keyProperty = "id" , useGeneratedKeys = true)
    @Insert("insert into todos(title , description , dueDate , status , userId) "
        + "values (#{title} , #{description} , #{dueDate} , #{status} , #{userId})"
    )
    void insert(Todo todo);

    //根据id删除todo
    @Delete("delete from todos where id = #{id}")
    void delete(Long id);

    //更新操作
    void update(Todo todo);

    //查询
    //查询所有待办事项
    @Select(
            "select id , title , description , dueDate , status , createdAt , updatedAt " +
                    "from todos where userId = #{userId} order by status"
    )
    List<Todo> getAll(Integer userId);

    //根据标题内容查询
    @Select(
            "select id, title, description, dueDate, status, createdAt, updatedAt " +
            "from todos " +
            "where title like concat('%', #{title}, '%') and userId = #{userId}"
    )
    List<Todo> getByTitle(@Param("title") String title , @Param("userId") Integer userId);

    //根据id查询
    @Select(
            "select id , title , description , dueDate , status , createdAt , updatedAt " +
                    "from todos where id = #{id} "
    )
    Todo getById(Long id);

    //根据截止时间或状态查询
    List<Todo> getByDueDateOrStatus(@Param("start") Date start, @Param("end") Date end, @Param("status") Integer status ,@Param("userId") Integer userId);


}
