package org.example.todolistandnotebook.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.pojo.User;

@Mapper
public interface UsersMapper {

    //注册新用户
    @Options(keyProperty = "id" , useGeneratedKeys = true)
    @Insert("insert into users(email, username, password) "
            + "values (#{email} , #{username} , #{password})"
    )
    void insertUser(User user);

    //根据id删除note
    @Delete("delete from users where id = #{id}")
    void deleteUser(Integer id);

    //修改密码
    @Update("update users set password = #{password} where id = #{id}")
    void updateUser(User user);

    //检验账号密码是否匹配
    @Select("select id , email ,username , password from users " +
            "where username = #{username} AND password = #{password}")
    User getUserByUsernameAndPassword(String username, String password);

    //通过id查询用户信息
    @Select("select id, email, username, password from users where id = #{id}")
    User getUserById(Integer id);

}
