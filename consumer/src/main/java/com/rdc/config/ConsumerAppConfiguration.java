package com.rdc.config;

import com.rdc.proxy.ProxyStrategy;
import com.rdc.serialization.SerializationStrategy;

/**
 * @author SD
 * @since 2018/4/21
 */
public class ConsumerAppConfiguration {
    private int workerGroupThreads = 4;

    private SerializationStrategy serializationStrategy = SerializationStrategy.JAVA_DEFAULT;

    private ProxyStrategy proxyStrategy = ProxyStrategy.JDK_DEFAULT;

    public int getWorkerGroupThreads() {
        return workerGroupThreads;
    }

    public ConsumerAppConfiguration setWorkerGroupThreads(int workerGroupThreads) {
        if (workerGroupThreads <= 0) {
            throw new IllegalArgumentException("worker group threads should not be negative, current : " + workerGroupThreads);
        }
        this.workerGroupThreads = workerGroupThreads;
        return this;
    }

    public SerializationStrategy getSerializationStrategy() {
        return serializationStrategy;
    }

    public ConsumerAppConfiguration setSerializationStrategy(SerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
        return this;
    }

    public ProxyStrategy getProxyStrategy() {
        return proxyStrategy;
    }

    public ConsumerAppConfiguration setProxyStrategy(ProxyStrategy proxyStrategy) {
        this.proxyStrategy = proxyStrategy;
        return this;
    }
}
