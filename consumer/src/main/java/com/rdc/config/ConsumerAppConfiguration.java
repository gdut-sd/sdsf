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

    private String registrantHost = "127.0.0.1";

    private int registrantPort = 2181;

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

    public String getRegistrantHost() {
        return registrantHost;
    }

    public ConsumerAppConfiguration setRegistrantHost(String registrantHost) {
        if (registrantHost == null || !registrantHost.matches("([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])")) {
            throw new IllegalArgumentException("invalid registrant host.");
        }
        this.registrantHost = registrantHost;
        return this;
    }

    public int getRegistrantPort() {
        return registrantPort;
    }

    public ConsumerAppConfiguration setRegistrantPort(int registrantPort) {
        if (registrantPort <= 0 || registrantPort > 65535) {
            throw new IllegalArgumentException("port should be within 1 and 65535, current : " + registrantPort);
        }
        this.registrantPort = registrantPort;
        return this;
    }
}
