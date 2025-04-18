package org.example.todolistandnotebook.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.todolistandnotebook.backend.pojo.Note;

import java.util.List;

@Mapper
public interface NotesMapper {

    //插入新note
    @Options(keyProperty = "id" , useGeneratedKeys = true)
    @Insert("insert into notes(title , content , userId) "
            + "values (#{title} , #{content} , #{userId})"
    )
    void insert(Note note);

    //根据id删除note
    @Delete("delete from notes where id = #{id}")
    void delete(Long id);

    //更新操作
    void update(Note note);

    //查询
    //查询所有待办事项
    @Select(
            "select id , title , content , createdAt , updatedAt " +
                    "from notes where userId = #{userId} order by updatedAt DESC"
    )
    List<Note> getAll(Integer userId);

    //根据标题内容查询
    @Select(
            "select id , title , content , createdAt , updatedAt " +
                    "from notes " +
                    "where title like concat('%', #{title}, '%') and userId = #{userId} order by updatedAt DESC "
    )
    List<Note> getByTitle(@Param("title") String title , Integer userId);

    //根据id查询
    @Select(
            "select id , title , content , createdAt , updatedAt " +
                    "from notes where id = #{id} "
    )
    Note getById(Long id);

    @Select("select id from notes where userId = #{userId}")
    List<Long> getByUserId(Integer userId);
}
