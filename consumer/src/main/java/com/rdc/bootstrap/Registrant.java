package com.rdc.bootstrap;

import com.rdc.exception.ServiceException;
import com.rdc.exception.ZkException;
import com.rdc.loadbalance.LoadBalanceStrategy;
import com.rdc.loadbalance.RoundRobin;
import org.apache.zookeeper.*;

import javax.xml.ws.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author SD
 * @since 2018/4/14
 */
public class Registrant implements Watcher {

    private CyclicBarrier initBarrier = new CyclicBarrier(2);

    private volatile ZooKeeper zooKeeper;

    private volatile boolean inited = false;

    public synchronized void init() {
        if (!inited) {
            try {
                zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000, this);
            } catch (IOException e) {
                throw new ZkException("zookeeper connection failed.", e);
            }
            initBarrier.reset();
            try {
                initBarrier.await();
                inited = true;
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

    public static void main(String[] args) throws InterruptedException, IOException, BrokenBarrierException, KeeperException {
        Registrant registrant = new Registrant();
        registrant.init();

        //System.out.println(registrant.getAvailableAddress("com.rdc.UserService", "0.0.1"));
    }
}
