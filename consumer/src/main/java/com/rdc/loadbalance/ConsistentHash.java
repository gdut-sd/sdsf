package com.rdc.loadbalance;

import com.rdc.exception.ServiceException;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author SD
 * @since 2018/4/22
 */
public class ConsistentHash implements LoadBalance {
    private Supplier<String> identitySupplier;
    private Function<Object, Integer> hashFunction;
    private int amountOfReplica;
    private SortedMap<Integer, String> nodes = new TreeMap<>();

    public ConsistentHash(Supplier<String> identitySupplier, Function<Object, Integer> hashFunction, Collection<String> availableAddresses) {
        this(identitySupplier, hashFunction, availableAddresses, 10);
    }

    public ConsistentHash(Supplier<String> identitySupplier, Function<Object, Integer> hashFunction, Collection<String> availableAddresses, int amountOfReplica) {
        if (identitySupplier == null) {
            throw new IllegalArgumentException("identity supplier should not be null.");
        }
        if (hashFunction == null) {
            throw new IllegalArgumentException("hash function should not be null.");
        }
        if (amountOfReplica < 0) {
            throw new IllegalArgumentException("amount of replica should not be negative.");
        }
        if (availableAddresses == null || availableAddresses.isEmpty()) {
            throw new ServiceException("no address available.");
        }

        this.identitySupplier = identitySupplier;
        this.hashFunction = hashFunction;
        this.amountOfReplica = amountOfReplica;

        for (String availableAddress : availableAddresses) {
            addNode(availableAddress);
        }
    }

    public void addNode(String node) {
        for (int i = 0; i < amountOfReplica; i++) {
            nodes.put(hashFunction.apply(node + i), node);
        }
    }

    @Override
    public String next() {
        int hash = hashFunction.apply(identitySupplier.get());
        if (nodes.containsKey(hash)) {
            return nodes.get(hash);
        }
        SortedMap<Integer, String> tm = nodes.tailMap(hash);
        return tm.isEmpty() ? nodes.get(nodes.firstKey()) : nodes.get(tm.firstKey());
    }
}
