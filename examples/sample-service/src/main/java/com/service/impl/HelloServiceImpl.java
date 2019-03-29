package com.service.impl;

import com.hello.HelloService;
import com.small.rpc.annotation.RpcService;


@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello, "+name;
    }
}
