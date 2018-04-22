package com.rdc.bootstrap;

import com.rdc.UserService;
import com.rdc.UserServiceImpl;
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

    // key : service name + version
    // value : producer implementation
    private Map<String, Object> producers;

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

    public Registrant register(Class<?> serviceClass, String version, Object serviceImpl) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (version == null) {
            throw new IllegalArgumentException("version should not be null.");
        }
        if (serviceImpl == null) {
            throw new IllegalArgumentException("service implementation should not be null.");
        }

        final String nodeName = serviceClass.getName() + ":" + version;
        producers.put(nodeName, serviceImpl);
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
        return producers.get(serviceName + ":" + version);
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
                for (Map.Entry<String, Object> entry : producers.entrySet()) {
                    addProducerNode(entry.getKey());
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

    public static void main(String[] args) throws Exception {
        Registrant registrant = new Registrant(8080);
        registrant.register(UserService.class, "0.0.1", new UserServiceImpl());
        registrant.init();

        Thread.sleep(10000000);
    }
}
