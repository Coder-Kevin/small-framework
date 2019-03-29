package com.small.rpc.service;

/**
 * 服务注册
 */
public interface ServiceRegistry {
    void register(String serviceName, String serviceAddress);
}
