package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String username;
    
    private static List<User> users = new ArrayList<>(Arrays.asList(
        new User("Rosa", "Marfil"),
        new User("Pepito", "Grillo"),
        new User("Manuela", "Rio")
    ));
    
    public User() {
        super();
    }
    
    public User(String name, String username) {
        super();
        this.name = name;
        this.username = username;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public static List<User> getUsers() {
        return users;
    }
    
    @Override
    public String toString() {
        return "User [name=" + name + ", username=" + username + "]";
    }
}