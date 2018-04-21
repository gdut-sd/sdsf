package com.rdc.bootstrap;

import com.rdc.UserService;
import com.rdc.UserServiceImpl;
import com.rdc.config.ProducerAppConfiguration;
import com.rdc.encode.LengthFieldBasedEncoder;
import com.rdc.handler.RpcCallHandler;
import com.rdc.serialization.JbossMarshallingFactory;
import com.rdc.serialization.SerializationStrategy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.zookeeper.KeeperException;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author SD
 */
public class ProducerApplication {
    public static void main(String[] args) {
        ProducerAppConfiguration configuration = new ProducerAppConfiguration();
        configuration.setSerializationStrategy(SerializationStrategy.JBOSS_MARSHALLING);
        ProducerApp app = new ProducerApp(configuration);
        app.register(UserService.class, "0.0.1", new UserServiceImpl());
        app.run();
    }
}
