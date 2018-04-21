package com.rdc.config;

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
}
