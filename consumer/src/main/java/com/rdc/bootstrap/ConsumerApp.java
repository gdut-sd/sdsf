package com.rdc.bootstrap;

import com.rdc.config.ConsumerAppConfiguration;
import com.rdc.connection.ConnectionCenter;
import com.rdc.proxy.ProxyCreator;

/**
 * @author SD
 * @since 2018/4/21
 */
public class ConsumerApp {
    private ConsumerAppConfiguration config;

    private ConnectionCenter connectionCenter;

    public ConsumerApp() {
        this(new ConsumerAppConfiguration());
    }

    public ConsumerApp(ConsumerAppConfiguration config) {
        this.config = config;
        connectionCenter = new ConnectionCenter(config);
        connectionCenter.init();
    }

    public <T> T getService(Class<T> serviceClazz, String version) {
        return ProxyCreator.getProxy(serviceClazz, version, config.getProxyStrategy(), connectionCenter);
    }
}
