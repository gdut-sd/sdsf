package com.rdc.loadbalance;

import com.rdc.exception.ServiceException;

import java.util.Collection;
import java.util.Random;

/**
 * @author SD
 * @since 2018/4/22
 */
public class RandomSelect implements LoadBalance {

    private String[] availableAddresses;

    private Random random;

    public RandomSelect(Collection<String> availableAddresses) {
        if (availableAddresses == null || availableAddresses.isEmpty()) {
            throw new ServiceException("no address available.");
        }

        this.availableAddresses = new String[availableAddresses.size()];
        int i = 0;
        for (String availableAddress : availableAddresses) {
            this.availableAddresses[i] = availableAddress;
            i++;
        }


        random = new Random(System.currentTimeMillis());
    }

    @Override
    public String next() {
        return availableAddresses[random.nextInt(availableAddresses.length)];
    }
}
