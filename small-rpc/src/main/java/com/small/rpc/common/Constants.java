package com.small.rpc.common;

public interface Constants {
    int ZK_SESSION_TIMEOUT = 50000;
    int ZK_CONNECTION_TIMEOUT = 1000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
