package com.rdc.bootstrap;

import com.rdc.connection.ConnectionCenter;
import com.rdc.model.RpcMessage;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author SD
 */
public class Sender {

    private Channel channel;

    public Sender(Channel channel) {
        this.channel = channel;
    }

    private Map<Long, CompletableFuture<Object>> resultMap = new ConcurrentHashMap<>();

    public Future<Object> send(Object message) {
        RpcMessage rpcMessage = (RpcMessage) message;
        CompletableFuture<Object> result = new CompletableFuture<>();
        resultMap.put(rpcMessage.getRpcCallId(), result);
        channel.writeAndFlush(rpcMessage);
        return result;
    }

    public void completeResponse(long rpcCallId, boolean success, Object result) {
        if (success) {
            resultMap.get(rpcCallId).complete(result);
        } else {
            resultMap.get(rpcCallId).completeExceptionally((Exception) result);
        }
    }

    public void disconnect() {
        if (channel.isActive()) {
            channel.close();
        }
    }

    public boolean isActive() {
        return channel.isActive();
    }
}
