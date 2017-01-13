package com.arx.springmvcangularjs.beans;

import java.util.UUID;

public class User {
	private String name;
	private String id;

	
	// Constructors
    public User() { 
    	this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
