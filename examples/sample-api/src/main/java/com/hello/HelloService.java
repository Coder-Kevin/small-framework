package com.hello;

import com.hello.model.User;

public interface HelloService {
    String hello(String name);

    User testGetUser(User user);
}
