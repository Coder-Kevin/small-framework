package com.service.impl;

import com.hello.HelloService;
import com.hello.model.User;
import com.small.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;


@RpcService(HelloService.class)
@Slf4j
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello, "+name;
    }

    @Override
    public User testGetUser(User user) {
        log.info("{}", user);
        User u = new User();
        u.setName("Kevin");
        return u;
    }
}
