package com.rdc.loadbalance;

/**
 * @author SD
 * @since 2018/4/22
 */
public enum LoadBalanceStrategy {
    RANDOM,
    ROUND_ROBIN,
    CONSISTENT_HASH
}
