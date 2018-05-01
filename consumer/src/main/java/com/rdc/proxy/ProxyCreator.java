package com.rdc.proxy;

import com.rdc.bootstrap.Registrant;
import com.rdc.connection.ConnectionCenter;
import com.rdc.loadbalance.LoadBalanceStrategy;

/**
 * @author SD
 */
public class ProxyCreator {

    public static <T> T getProxy(Class<T> serviceClass,
                                 String version,
                                 ProxyStrategy proxyStrategy,
                                 LoadBalanceStrategy loadBalanceStrategy,
                                 ConnectionCenter connectionCenter,
                                 Registrant registrant,
                                 int autoRetryTimes,
                                 int timeoutMillis) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (connectionCenter == null) {
            throw new IllegalArgumentException("connection center should not be null.");
        }
        if (registrant == null) {
            throw new IllegalArgumentException("registrant should not be null.");
        }
        if (autoRetryTimes < 0 || autoRetryTimes > 100) {
            throw new IllegalArgumentException("auto retry time should be within 0 and 100");
        }
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeout should not be negative.");
        }

        switch (proxyStrategy) {
            case JDK_DEFAULT:
                return new JdkDynamicProxyInvoker<>(serviceClass, version, connectionCenter, registrant, loadBalanceStrategy, autoRetryTimes, timeoutMillis).get();
            case CGLIB:
                return new CglibProxyInvoker<>(serviceClass, version, connectionCenter, registrant, loadBalanceStrategy, autoRetryTimes, timeoutMillis).get();
            default:
                return null;
        }
    }
}
