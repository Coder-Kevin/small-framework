package com.service.impl;

import com.hello.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, "+name;
    }
}
