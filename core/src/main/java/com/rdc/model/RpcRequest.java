package com.rdc.model;

import java.util.concurrent.CompletableFuture;

/**
 * @author SD
 */
public class RpcRequest {

    private RpcMessage rpcMessage;

    private CompletableFuture result;

    public RpcMessage getRpcMessage() {
        return rpcMessage;
    }

    public void setRpcMessage(RpcMessage rpcMessage) {
        this.rpcMessage = rpcMessage;
    }

    public CompletableFuture getResult() {
        return result;
    }

    public void setResult(CompletableFuture result) {
        this.result = result;
    }
}
