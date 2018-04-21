package com.rdc.handler;

import com.rdc.connection.ConnectionCenter;
import com.rdc.model.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * @author SD
 */
public class RpcCallHandler extends SimpleChannelInboundHandler<RpcMessage> implements ChannelOutboundHandler {

    private ConnectionCenter connectionCenter;

    public RpcCallHandler(ConnectionCenter connectionCenter) {
        this.connectionCenter = connectionCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage) {
        connectionCenter.completeResult(channelHandlerContext.channel().remoteAddress().toString().substring(1), rpcMessage.getRpcCallId(), rpcMessage.isSuccess(), rpcMessage.getBody());
    }

    @Override
    public void bind(ChannelHandlerContext channelHandlerContext, SocketAddress socketAddress, ChannelPromise channelPromise) {
        channelHandlerContext.bind(socketAddress, channelPromise);
    }

    @Override
    public void connect(ChannelHandlerContext channelHandlerContext, SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
        channelHandlerContext.connect(socketAddress, socketAddress1, channelPromise);
    }

    @Override
    public void disconnect(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) {
        connectionCenter.disconnect(channelHandlerContext.channel().remoteAddress().toString().substring(1));
    }

    @Override
    public void close(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) {
        channelHandlerContext.close(channelPromise);
    }

    @Override
    public void deregister(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) {
        channelHandlerContext.close(channelPromise);
    }

    @Override
    public void read(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.read();
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) {
        channelHandlerContext.write(o);
    }

    @Override
    public void flush(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectionCenter.disconnect(ctx.channel().remoteAddress().toString().substring(1));
        cause.printStackTrace();
    }
}
