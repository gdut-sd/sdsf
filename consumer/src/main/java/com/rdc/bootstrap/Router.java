package com.rdc.bootstrap;

import com.rdc.connection.ConnectionCenter;
import com.rdc.loadbalance.LoadBalanceStrategy;
import com.rdc.loadbalance.RoundRobin;
import com.rdc.model.RpcMessage;
import com.sun.istack.internal.NotNull;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author SD
 * @since 2018/4/15
 */
public class Router implements Watcher {
    private Map<String, LoadBalanceStrategy> availableAddresses = new ConcurrentHashMap<>();

    private ConnectionCenter connectionCenter;

    private Registrant registrant;

    public Router(ConnectionCenter connectionCenter, Registrant registrant) {
        if (connectionCenter == null) {
            throw new IllegalArgumentException("connection center should not be null.");
        }
        if (registrant == null) {
            throw new IllegalArgumentException("registrant should not be null.");
        }

        this.connectionCenter = connectionCenter;
        this.registrant = registrant;
    }

    public Future<Object> send(String service, String version, RpcMessage message) {
        final String key = service + ":" + version;
        LoadBalanceStrategy lb = availableAddresses.get(key);
        if (lb == null) {
            lb = new RoundRobin(registrant.getAvailableAddress(service, version, this));
            availableAddresses.putIfAbsent(key, lb);
        }
        String[] s = lb.next().split(":");
        Sender sender = connectionCenter.connect(s[0], Integer.parseInt(s[1]));
        return sender.send(message);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            String[] paths = watchedEvent.getPath().split("/");
            for (String path : paths) {
                if (path.contains(":")) {
                    availableAddresses.remove(path);
                }
            }
        }
    }
}
