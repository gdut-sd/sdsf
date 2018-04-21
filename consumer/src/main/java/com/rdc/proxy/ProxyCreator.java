package com.rdc.proxy;

import com.rdc.connection.ConnectionCenter;
import com.rdc.invoker.JdkDynamicProxyInvoker;

/**
 * @author SD
 */
public class ProxyCreator {

    public static <T> T getProxy(Class<T> interfaceClazz, String version, ProxyStrategy proxyStrategy, ConnectionCenter connectionCenter) {
        if (proxyStrategy == ProxyStrategy.JDK_DEFAULT) {
            return new JdkDynamicProxyInvoker<>(connectionCenter, interfaceClazz, version).get();
        }

        return null;
    }
}
