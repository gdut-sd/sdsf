package com.rdc.loadbalance;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author SD
 * @since 2018/4/14
 */
public class RoundRobin implements LoadBalanceStrategy {

    private String[] availableAddresses;

    private AtomicInteger counter = new AtomicInteger(0);

    public RoundRobin(Collection<String> availableAddresses) {
        if (availableAddresses == null || availableAddresses.isEmpty()) {
            throw new RuntimeException("no address available.");
        }
        this.availableAddresses = new String[availableAddresses.size()];
        int i = 0;
        for (String availableAddress : availableAddresses) {
            this.availableAddresses[i] = availableAddress;
            i++;
        }
    }

    @Override
    public String next() {
        return availableAddresses[counter.getAndIncrement() % availableAddresses.length];
    }
}
