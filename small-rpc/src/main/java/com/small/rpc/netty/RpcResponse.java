package com.small.rpc.netty;

import lombok.Data;

/**
 * 封装RPC响应
 */
@Data
public class RpcResponse {

    private String requestId;

    private Throwable throwable;

    private Object result;

}
