package com.small.rpc.netty;

import lombok.Data;

/**
 * 封装RPC请求
 */
@Data
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
