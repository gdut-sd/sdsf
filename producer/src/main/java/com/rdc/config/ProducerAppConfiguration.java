package com.rdc.config;

import com.rdc.bootstrap.ProducerApp;
import com.rdc.serialization.SerializationStrategy;

/**
 * @author SD
 * @since 2018/4/21
 */
public class ProducerAppConfiguration {
    private int port = 8080;

    private int bossGroupThreads = 4;

    private int workerGroupThreads = 4;

    private SerializationStrategy serializationStrategy = SerializationStrategy.JAVA_DEFAULT;

    private HandlingStrategy handlingStrategy = HandlingStrategy.SYNC;

    private String registrantHost = "127.0.0.1";

    private int registrantPort = 2181;

    public int getPort() {
        return port;
    }

    public ProducerAppConfiguration setPort(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port should be within 1 and 65535, current : " + port);
        }
        this.port = port;
        return this;
    }

    public int getBossGroupThreads() {
        return bossGroupThreads;
    }

    public ProducerAppConfiguration setBossGroupThreads(int bossGroupThreads) {
        if (bossGroupThreads <= 0) {
            throw new IllegalArgumentException("boss group threads should not be negative, current : " + bossGroupThreads);
        }
        this.bossGroupThreads = bossGroupThreads;
        return this;
    }

    public int getWorkerGroupThreads() {
        return workerGroupThreads;
    }

    public ProducerAppConfiguration setWorkerGroupThreads(int workerGroupThreads) {
        if (workerGroupThreads <= 0) {
            throw new IllegalArgumentException("worker group threads should not be negative, current : " + workerGroupThreads);
        }
        this.workerGroupThreads = workerGroupThreads;
        return this;
    }

    public SerializationStrategy getSerializationStrategy() {
        return serializationStrategy;
    }

    public ProducerAppConfiguration setSerializationStrategy(SerializationStrategy serializationStrategy) {
        if (serializationStrategy == null) {
            throw new IllegalArgumentException("serialization strategy should not be null.");
        }
        this.serializationStrategy = serializationStrategy;
        return this;
    }

    public HandlingStrategy getHandlingStrategy() {
        return handlingStrategy;
    }

    public ProducerAppConfiguration setHandlingStrategy(HandlingStrategy handlingStrategy) {
        if (handlingStrategy == null) {
            throw new IllegalArgumentException("handling strategy should not be null");
        }
        this.handlingStrategy = handlingStrategy;
        return this;
    }

    public String getRegistrantHost() {
        return registrantHost;
    }

    public ProducerAppConfiguration setRegistrantHost(String registrantHost) {
        if (registrantHost == null || !registrantHost.matches("([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])\\.([12]?[0-9]?[0-9])")) {
            throw new IllegalArgumentException("invalid registrant host.");
        }
        this.registrantHost = registrantHost;
        return this;
    }

    public int getRegistrantPort() {
        return registrantPort;
    }

    public ProducerAppConfiguration setRegistrantPort(int registrantPort) {
        if (registrantPort <= 0 || registrantPort > 65535) {
            throw new IllegalArgumentException("port should be within 1 and 65535, current : " + registrantPort);
        }
        this.registrantPort = registrantPort;
        return this;
    }
}
