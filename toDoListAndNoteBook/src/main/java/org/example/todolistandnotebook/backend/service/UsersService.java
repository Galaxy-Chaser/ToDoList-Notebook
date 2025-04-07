package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.mapper.UsersMapper;
import org.example.todolistandnotebook.backend.pojo.User;
import org.example.todolistandnotebook.backend.service.IService.UsersIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UsersIService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public User InsertUsers(User user) {
        usersMapper.insertUser(user);
        User newUser = usersMapper.getUserById(user.getId());
        return newUser;
    }

    @Override
    public User UpdateUsers(User user) {
        usersMapper.updateUser(user);
        User UpdatedUser = usersMapper.getUserById(user.getId());
        return UpdatedUser;
    }

    @Override
    public void DeleteUsers(Integer id) {
        usersMapper.deleteUser(id);
    }

    @Override
    public User LoginUsers(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        User tempUser = usersMapper.getUserByUsernameAndPassword(username, password);
        return tempUser;
    }
}
