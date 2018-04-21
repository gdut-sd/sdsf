package com.rdc.bootstrap;

import com.rdc.UserServiceImpl;
import com.rdc.exception.ZkException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import javax.swing.plaf.TableHeaderUI;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
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

    private volatile boolean inited = false;

    public Registrant(int port) {
        this.port = port;
        producers = new ConcurrentHashMap<>();
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace(); // TODO exception handle
        }
    }

    public Registrant register(String serviceName, String version, Object producerImpl) {
        final String nodeName = serviceName + ":" + version;
        producers.put(nodeName, producerImpl);
        if (inited) {
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
        if (!inited) {
            try {
                zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000, this);
                initBarrier.reset();
                initBarrier.await();

                // add nodes on zk
                if (zooKeeper.exists("/sdsf", false) == null) {
                    zooKeeper.create("/sdsf", DUMMY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                for (Map.Entry<String, Object> entry : producers.entrySet()) {
                    addProducerNode(entry.getKey());
                }

                inited = true;
            } catch (IOException | KeeperException e) {
                throw new ZkException(e);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace(); // TODO exception handle
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            initBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace(); // TODO exception handle
        }
    }

    public static void main(String[] args) throws Exception {
        //new Registrant().run();
        Registrant registrant = new Registrant(8080);
        registrant.register("com.rdc.UserService", "0.0.1", new UserServiceImpl());
        registrant.init();

        Thread.sleep(10000000);
    }
}
