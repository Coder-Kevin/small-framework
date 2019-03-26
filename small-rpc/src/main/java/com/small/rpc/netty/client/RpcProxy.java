package com.small.rpc.netty.client;

import com.small.rpc.netty.RpcRequest;
import com.small.rpc.netty.RpcResponse;
import com.small.rpc.service.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcProxy {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

                    if (serviceDiscovery != null) {
//                            serverAddress = serviceDiscovery.discover(); // 发现服务
                    }

                    String[] array = serverAddress.split(":");
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);

                    RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
                    RpcResponse response = client.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                    if (response.getThrowable() != null) {
                        throw response.getThrowable();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }
}
