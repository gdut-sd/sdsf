package com.rdc.connection;

import com.rdc.bootstrap.Sender;
import com.rdc.config.ConsumerAppConfiguration;
import com.rdc.encode.LengthFieldBasedEncoder;
import com.rdc.exception.ConnectionException;
import com.rdc.handler.RpcCallHandler;
import com.rdc.serialization.JbossMarshallingFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SD
 * @since 2018/4/12
 */
public class ConnectionCenter {
    private ConsumerAppConfiguration config;

    private Map<String, Sender> activeSenders = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    public ConnectionCenter(ConsumerAppConfiguration config) {
        this.config = config;
        init();
    }

    public void init() {
        EventLoopGroup group = new NioEventLoopGroup(config.getWorkerGroupThreads());

        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new LengthFieldBasedEncoder());

                switch (config.getSerializationStrategy()) {
                    case JAVA_DEFAULT:
                        channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE - 16,
                                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                .addLast(new ObjectEncoder());
                        break;
                    case JBOSS_MARSHALLING:
                        channel.pipeline()
                                .addLast(JbossMarshallingFactory.newMarshallingDecoder())
                                .addLast(JbossMarshallingFactory.newMarshallingEncoder());
                        break;
                }
                channel.pipeline().addLast(new RpcCallHandler(ConnectionCenter.this));
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
    }

    public void disconnect(String host, int port) {
        String key = host + ":" + port;
        disconnect(key);
    }

    public void disconnect(String socketAddress) {
        Sender sender = activeSenders.remove(socketAddress);
        if (sender != null) {
            sender.disconnect();
        }
    }

    public Sender connect(String host, int port) {
        String key = host + ":" + port;
        Sender sender = activeSenders.get(key);
        if (sender != null) {
            if (!sender.isActive()) {
                activeSenders.remove(key);
            } else {
                return sender;
            }
        }

        try {
            Channel c = bootstrap.connect(host, port).sync().channel();
            c.closeFuture().addListener(future -> {
                if (future.isSuccess()) {
                    disconnect(host, port);
                }
            });
            sender = new Sender(c);
            activeSenders.put(key, sender);
            return sender;
        } catch (InterruptedException e) {
            throw new ConnectionException("connection to concrete service provider was interrupted.", e);
        }
    }

    public void completeResult(String socketAddress, long rpcCallId, boolean success, Object result) {
        Sender sender = activeSenders.get(socketAddress);
        if (sender != null) {
            sender.completeResponse(rpcCallId, success, result);
        }
    }
}
