package com.arx.springmvcangularjs.service;

import java.util.List;

import com.arx.springmvcangularjs.beans.User;

public interface UserService {
    public List<User> getAllUsers();

    public void addUser(User user);

    public void deleteUser(String id);

    public void deleteAll();
}
