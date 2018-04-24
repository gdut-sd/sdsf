package com.rdc.handler;

import com.rdc.bootstrap.Registrant;
import com.rdc.config.HandlingStrategy;
import com.rdc.exception.InvocationException;
import com.rdc.exception.ServiceException;
import com.rdc.model.RpcCallRequest;
import com.rdc.model.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @author SD
 */
public class RpcCallHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private Registrant registrant;

    private ExecutorService asyncPool;

    public RpcCallHandler(Registrant registrant, ExecutorService asyncPool) {
        this.registrant = registrant;
        this.asyncPool = asyncPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage rpcMessage) {
        System.out.println(rpcMessage);
        RpcCallRequest request = (RpcCallRequest) rpcMessage.getBody();
        System.out.println(request.getInterfaceName());

        HandlingStrategy handlingStrategy = registrant.getProducerHandlingStrategy(request.getInterfaceName(), request.getVersion());
        switch (handlingStrategy) {
            case SYNC:
                try {
                    Object producer = registrant.getProducer(request.getInterfaceName(), request.getVersion());
                    if (producer == null) {
                        Exception ex = new ServiceException("no available service found.");
                        rpcMessage.setSuccess(false);
                        rpcMessage.setBody(ex);
                        ctx.channel().writeAndFlush(rpcMessage);
                        break;
                    }
                    Class<?> producerClass = producer.getClass();
                    @SuppressWarnings("unchecked")
                    Method m = producerClass.getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
                    m.invoke(producer, request.getArgs());
                    rpcMessage.setSuccess(true);
                    rpcMessage.setBody(m.invoke(producer, request.getArgs()));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Exception ex = new InvocationException("method invocation failed, please check the version of the service.", e);
                    rpcMessage.setSuccess(false);
                    rpcMessage.setBody(ex);
                }
                ctx.channel().writeAndFlush(rpcMessage);
                break;
            case ASYNC:
                asyncPool.execute(() -> {
                    try {
                        Object producer = registrant.getProducer(request.getInterfaceName(), request.getVersion());
                        if (producer == null) {
                            Exception ex = new ServiceException("no available service found.");
                            rpcMessage.setSuccess(false);
                            rpcMessage.setBody(ex);
                            ctx.channel().writeAndFlush(rpcMessage);
                            return;
                        }
                        Class<?> producerClass = producer.getClass();
                        @SuppressWarnings("unchecked")
                        Method m = producerClass.getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
                        m.invoke(producer, request.getArgs());
                        rpcMessage.setSuccess(true);
                        rpcMessage.setBody(m.invoke(producer, request.getArgs()));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        Exception ex = new ServiceException("method invocation failed, please check the version of the service.", e);
                        rpcMessage.setSuccess(false);
                        rpcMessage.setBody(ex);
                    }
                    ctx.channel().writeAndFlush(rpcMessage);
                });
                break;
            default:
                Exception ex = new InvocationException("no available service found.");
                rpcMessage.setSuccess(false);
                rpcMessage.setBody(ex);
                ctx.channel().writeAndFlush(rpcMessage);
        }
    }
}
