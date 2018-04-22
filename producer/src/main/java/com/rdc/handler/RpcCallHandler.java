package com.rdc.handler;

import com.rdc.bootstrap.Registrant;
import com.rdc.exception.InvocationException;
import com.rdc.model.RpcCallRequest;
import com.rdc.model.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author SD
 */
public class RpcCallHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private Registrant registrant;

    public RpcCallHandler(Registrant registrant) {
        this.registrant = registrant;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage rpcMessage) {
        System.out.println(rpcMessage);

        RpcCallRequest request = (RpcCallRequest) rpcMessage.getBody();

        System.out.println(request.getInterfaceName());
        Object producer = registrant.getProducer(request.getInterfaceName(), request.getVersion());
        Class producerClass = producer.getClass();
        try {
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
    }
}
