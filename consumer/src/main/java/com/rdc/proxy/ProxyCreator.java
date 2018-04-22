package com.rdc.proxy;

import com.rdc.bootstrap.Registrant;
import com.rdc.connection.ConnectionCenter;
import com.rdc.invoker.JdkDynamicProxyInvoker;

/**
 * @author SD
 */
public class ProxyCreator {

    public static <T> T getProxy(Class<T> interfaceClazz, String version, ProxyStrategy proxyStrategy, ConnectionCenter connectionCenter, Registrant registrant) {
        if (proxyStrategy == ProxyStrategy.JDK_DEFAULT) {
            return new JdkDynamicProxyInvoker<>(interfaceClazz, version, connectionCenter, registrant).get();
        }

        return null;
    }
}
