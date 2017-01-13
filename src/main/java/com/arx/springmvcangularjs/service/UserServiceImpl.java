package com.arx.springmvcangularjs.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.arx.springmvcangularjs.beans.User;


@Service("userService")
public class UserServiceImpl implements UserService {

    private static List<User> userList = new ArrayList<User>();

    @Override
    public List<User> getAllUsers() {
        return userList;
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }

    @Override
    public void deleteUser(String id) {

    	for (int i=0; i<userList.size(); i++) {
    		User u = userList.get(i);
    		String uId = u.getId();
    		if(uId.equals(id)){
    			userList.remove(u);

    		}
    	}
    }

    @Override
    public void deleteAll() {
        userList.clear();
    }
}

