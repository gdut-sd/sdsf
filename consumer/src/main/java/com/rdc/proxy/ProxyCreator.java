package com.rdc.proxy;

import com.rdc.bootstrap.Registrant;
import com.rdc.connection.ConnectionCenter;
import com.rdc.loadbalance.LoadBalanceStrategy;

/**
 * @author SD
 */
public class ProxyCreator {

    public static <T> T getProxy(Class<T> serviceClass, String version, ProxyStrategy proxyStrategy, LoadBalanceStrategy loadBalanceStrategy, ConnectionCenter connectionCenter, Registrant registrant) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (connectionCenter == null) {
            throw new IllegalArgumentException("connection center should not be null.");
        }
        if (registrant == null) {
            throw new IllegalArgumentException("registrant should not be null.");
        }

        if (proxyStrategy == ProxyStrategy.JDK_DEFAULT) {
            return new JdkDynamicProxyInvoker<>(serviceClass, version, connectionCenter, registrant, loadBalanceStrategy).get();
        }

        return null;
    }
}
