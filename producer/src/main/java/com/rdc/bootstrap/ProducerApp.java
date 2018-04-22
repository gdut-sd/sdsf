package com.rdc.bootstrap;

import com.rdc.config.ProducerAppConfiguration;
import com.rdc.encode.LengthFieldBasedEncoder;
import com.rdc.exception.ConnectionException;
import com.rdc.handler.RpcCallHandler;
import com.rdc.serialization.JbossMarshallingFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author SD
 * @since 2018/4/21
 */
public class ProducerApp implements Runnable {
    private ProducerAppConfiguration config;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private Registrant registrant;

    public ProducerApp() {
        this(new ProducerAppConfiguration());
    }

    public ProducerApp(ProducerAppConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("configuration should not be null.");
        }
        this.config = config;
        bossGroup = new NioEventLoopGroup(config.getBossGroupThreads());
        workerGroup = new NioEventLoopGroup(config.getWorkerGroupThreads());

        registrant = new Registrant(config.getPort());
    }

    @Override
    public void run() {
        registrant.init();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1042);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
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

                    channel.pipeline().addLast(new RpcCallHandler(registrant));
                }
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }));

            b.bind(config.getPort())
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            throw new ConnectionException("provider binding port was interrupted.", e);
        }
    }

    public <T> ProducerApp register(Class<T> serviceClass, String version, T serviceImpl) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (version == null) {
            throw new IllegalArgumentException("version should not be null.");
        }
        if (serviceImpl == null) {
            throw new IllegalArgumentException("service implementation should not be null.");
        }

        registrant.register(serviceClass, version, serviceImpl);
        return this;
    }
}
