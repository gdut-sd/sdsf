package com.rdc;

import com.rdc.bootstrap.ConsumerApp;
import com.rdc.config.ConsumerAppConfiguration;
import com.rdc.proxy.ProxyStrategy;
import com.rdc.serialization.SerializationStrategy;

/**
 *
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        ConsumerAppConfiguration config = new ConsumerAppConfiguration();
        config.setSerializationStrategy(SerializationStrategy.JBOSS_MARSHALLING)
                .setProxyStrategy(ProxyStrategy.CGLIB);
        ConsumerApp app = new ConsumerApp(config);
        UserService userService = app.getService(UserService.class, "0.0.1", 1000);
        System.out.println(userService.getUser(101));
        //Thread.sleep(10000);
        System.out.println(userService.getUser(102));
    }
}
