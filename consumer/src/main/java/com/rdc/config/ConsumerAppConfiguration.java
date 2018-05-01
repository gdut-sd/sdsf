package com.rdc.config;

import com.rdc.loadbalance.LoadBalanceStrategy;
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

    private LoadBalanceStrategy loadBalanceStrategy = LoadBalanceStrategy.ROUND_ROBIN;

    private int autoRetryTimes = 3;

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

    public LoadBalanceStrategy getLoadBalanceStrategy() {
        return loadBalanceStrategy;
    }

    public ConsumerAppConfiguration setLoadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        if (loadBalanceStrategy == null) {
            throw new IllegalArgumentException("load balance strategy should not be null.");
        }
        this.loadBalanceStrategy = loadBalanceStrategy;
        return this;
    }

    public int getAutoRetryTimes() {
        return autoRetryTimes;
    }

    public ConsumerAppConfiguration setAutoRetryTimes(int autoRetryTimes) {
        if (autoRetryTimes < 0 || autoRetryTimes > 100) {
            throw new IllegalArgumentException("auto retry time should be within 0 and 100");
        }
        this.autoRetryTimes = autoRetryTimes;
        return this;
    }
}
