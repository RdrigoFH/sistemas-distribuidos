package com.example.soap;

import java.util.List;
import javax.jws.WebService;
import com.example.model.User;

@WebService(endpointInterface = "com.example.soap.SOAPI")
public class SOAPIimpl implements SOAPI {

    @Override
    public List<User> getUsers() {
        return User.getUsers();
    }

    @Override
    public void addUser(User user) {
        User.getUsers().add(user);
    }
}
