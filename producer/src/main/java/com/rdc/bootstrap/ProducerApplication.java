package com.rdc.bootstrap;

import com.rdc.UserService;
import com.rdc.UserServiceImpl;
import com.rdc.config.HandlingStrategy;
import com.rdc.config.ProducerAppConfiguration;
import com.rdc.serialization.SerializationStrategy;

/**
 * @author SD
 */
public class ProducerApplication {
    public static void main(String[] args) {
        ProducerAppConfiguration configuration = new ProducerAppConfiguration();
        configuration.setSerializationStrategy(SerializationStrategy.JBOSS_MARSHALLING);
        ProducerApp app = new ProducerApp(configuration);
        app.register(UserService.class, "0.0.1", new UserServiceImpl(), HandlingStrategy.ASYNC);
        app.run();
    }
}
