package com.rdc.bootstrap;

import com.rdc.UserService;
import com.rdc.UserServiceImpl;
import com.rdc.config.HandlingStrategy;
import com.rdc.exception.ZkException;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/**
 * @author SD
 */
public class Registrant implements Watcher {

    private static final byte[] DUMMY_CONTENT = new byte[]{};

    private String host;

    private int port;

    private volatile ZooKeeper zooKeeper;

    private CyclicBarrier initBarrier = new CyclicBarrier(2);

    // key : service name + ":" + version
    // value : producer wrapper
    private Map<String, ProducerWrapper> producers;

    private volatile boolean initialized = false;

    public Registrant(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port should be within 1 and 65535, current : " + port);
        }

        this.port = port;
        producers = new ConcurrentHashMap<>();
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("no local host available.", e);
        }
    }

    public Registrant register(Class<?> serviceClass, String version, Object serviceImpl, HandlingStrategy handlingStrategy) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (version == null) {
            throw new IllegalArgumentException("version should not be null.");
        }
        if (serviceImpl == null) {
            throw new IllegalArgumentException("service implementation should not be null.");
        }
        if (handlingStrategy == null) {
            handlingStrategy = HandlingStrategy.SYNC;
        }

        final String nodeName = serviceClass.getName() + ":" + version;
        producers.put(nodeName, new ProducerWrapper(handlingStrategy, serviceImpl));
        if (initialized) {
            addProducerNode(nodeName);
        }
        return this;
    }

    private void addProducerNode(String nodeName) {
        try {
            if (zooKeeper.exists("/sdsf", false) == null) {
                zooKeeper.create("/sdsf", DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            if (zooKeeper.exists("/sdsf/" + nodeName, false) == null) {
                zooKeeper.create("/sdsf/" + nodeName, DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            if (zooKeeper.exists("/sdsf/" + nodeName + "/producer", false) == null) {
                zooKeeper.create("/sdsf/" + nodeName + "/producer", DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            try {
                zooKeeper.create("/sdsf/" + nodeName + "/producer/" + host + ":" + port, DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (KeeperException.NodeExistsException e) {
                // ignored
            }
        } catch (InterruptedException | KeeperException e) {
            throw new ZkException(e);
        }
    }

    public Object getProducer(String serviceName, String version) {
        ProducerWrapper wrapper = producers.get(serviceName + ":" + version);
        if (wrapper != null) {
            return wrapper.getProducer();
        }
        return null;
    }

    public HandlingStrategy getProducerHandlingStrategy(String serviceName, String version) {
        ProducerWrapper wrapper = producers.get(serviceName + ":" + version);
        if (wrapper != null) {
            return wrapper.getHandlingStrategy();
        }
        return null;
    }

    public synchronized void init() {
        if (!initialized) {
            try {
                zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000, this);
                initBarrier.reset();

                try {
                    initBarrier.await();
                } catch (InterruptedException e) {
                    throw new ZkException("zookeeper sync interrupted.", e);
                } catch (BrokenBarrierException e) {
                    throw new ZkException("zookeeper sync exception", e);
                }

                // add nodes on zk
                if (zooKeeper.exists("/sdsf", false) == null) {
                    zooKeeper.create("/sdsf", DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                for (String k : producers.keySet()) {
                    addProducerNode(k);
                }

                initialized = true;
            } catch (KeeperException | InterruptedException e) {
                throw new ZkException(e);
            } catch (IOException e) {
                throw new ZkException("zookeeper connection failed.", e);
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            initBarrier.await();
        } catch (InterruptedException e) {
            throw new ZkException("zookeeper sync interrupted.", e);
        } catch (BrokenBarrierException e) {
            throw new ZkException("zookeeper sync exception", e);
        }
    }
}

class ProducerWrapper {
    HandlingStrategy handlingStrategy;
    Object producer;

    public ProducerWrapper(HandlingStrategy handlingStrategy, Object producer) {
        this.handlingStrategy = handlingStrategy;
        this.producer = producer;
    }

    public HandlingStrategy getHandlingStrategy() {
        return handlingStrategy;
    }

    public Object getProducer() {
        return producer;
    }
}
