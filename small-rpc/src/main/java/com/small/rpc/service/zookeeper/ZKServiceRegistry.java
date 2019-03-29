package com.small.rpc.service.zookeeper;

import com.small.rpc.common.Constants;
import com.small.rpc.service.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    private ZkClient zkClient;

    public ZKServiceRegistry(String registryAddress){
        zkClient = new ZkClient(registryAddress, Constants.ZK_SESSION_TIMEOUT, Constants.ZK_CONNECTION_TIMEOUT);
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constants.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.debug("create address node: {}", addressNode);
    }
}
