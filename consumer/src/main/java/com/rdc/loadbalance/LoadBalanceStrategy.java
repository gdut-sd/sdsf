package com.rdc.loadbalance;

import java.util.Collection;

/**
 * @author SD
 * @since 2018/4/14
 */
public interface LoadBalanceStrategy {
    String next();
}
