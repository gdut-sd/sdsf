package com.rdc.loadbalance;

/**
 * @author SD
 * @since 2018/4/14
 */
public interface LoadBalanceStrategy {
    String next();
}
