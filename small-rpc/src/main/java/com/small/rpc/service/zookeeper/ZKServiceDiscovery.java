package com.small.rpc.service.zookeeper;

import com.small.rpc.common.Constants;
import com.small.rpc.service.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ZKServiceDiscovery implements ServiceDiscovery {

    private final String registryAddress;

    public ZKServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Override
    public String discover(String name) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(registryAddress, Constants.ZK_SESSION_TIMEOUT, Constants.ZK_CONNECTION_TIMEOUT);
        log.debug("connect zookeeper");
        try {
            // 获取 service 节点
            String servicePath = Constants.ZK_REGISTRY_PATH + "/" + name;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtils.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            // 获取 address 节点
            String address;
            int size = addressList.size();
            if (size == 1) {
                // 若只有一个地址，则获取该地址
                address = addressList.get(0);
                log.debug("get only address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                log.debug("get random address node: {}", address);
            }
            // 获取 address 节点的值
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }

}
