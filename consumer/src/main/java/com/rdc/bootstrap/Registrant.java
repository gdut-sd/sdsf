package com.rdc.bootstrap;

import com.rdc.exception.ServiceException;
import com.rdc.exception.ZkException;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author SD
 * @since 2018/4/14
 */
public class Registrant implements Watcher {

    private CyclicBarrier initBarrier = new CyclicBarrier(2);

    private volatile ZooKeeper zooKeeper;

    private volatile boolean initialized = false;

    public Registrant(String zkHost, int zkPort) {
        init(zkHost, zkPort);
    }

    public synchronized void init(String zkHost, int zkPort) {
        if (!initialized) {
            try {
                zooKeeper = new ZooKeeper(zkHost + ":" + zkPort, 1000, this);
            } catch (IOException e) {
                throw new ZkException("zookeeper connection failed.", e);
            }
            initBarrier.reset();
            try {
                initBarrier.await();
                initialized = true;
            } catch (InterruptedException e) {
                throw new ZkException("zookeeper sync interrupted.", e);
            } catch (BrokenBarrierException e) {
                throw new ZkException("zookeeper sync exception", e);
            }
        }
    }

    public List<String> getAvailableAddress(String service, String version, Router router) {
        final String key = "/sdsf/" + service + ":" + version + "/producer";

        try {
            return zooKeeper.getChildren(key, router);
        } catch (KeeperException.NoNodeException e) {
            throw new ServiceException("available service not found.");
        } catch (InterruptedException | KeeperException e) {
            throw new ZkException(e);
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
