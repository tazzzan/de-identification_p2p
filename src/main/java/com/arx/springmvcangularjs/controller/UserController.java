package com.arx.springmvcangularjs.controller;


import com.arx.springmvcangularjs.beans.User;
import com.arx.springmvcangularjs.service.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

	    @Autowired
	    private UserServiceImpl userService;

	    @RequestMapping("userlist.json")
	    public @ResponseBody List<User> getUserList() {
	        return userService.getAllUsers();
	    }

	    @RequestMapping(value = "/add", method = RequestMethod.POST)
	    public @ResponseBody void addUser(@RequestBody User user) {
	        userService.addUser(user);
	    }

	    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
	    public @ResponseBody void removeUser(@PathVariable("id") String id) {
	        userService.deleteUser(id);
	    }

	    @RequestMapping(value = "/removeAll", method = RequestMethod.DELETE)
	    public @ResponseBody void removeAllUsers() {
	        userService.deleteAll();
	    }

	    @RequestMapping("/layout")
	    public String getUserPartialPage() {
	        return "users/layout";
	    }
	    
	    @RequestMapping("/customers/layout")
	    public String getCustomerPartialPage() {
	        return "users/customers/layout";
	    }
	    
	    
	    @RequestMapping("/providers/layout")
	    public String getProviderPartialPage() {
	        return "users/providers/layout";
	    }
}


