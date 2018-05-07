package com.rdc.model;

import java.io.Serializable;

/**
 * @author SD
 */
public class RpcMessage implements Serializable {

    private static final long serialVersionUID = -1544933624460288374L;

    private long rpcCallId;

    private boolean success;

    private Object body;

    public RpcMessage() {
    }

    public RpcMessage(long rpcCallId, Object body) {
        this.rpcCallId = rpcCallId;
        this.body = body;
    }

    public long getRpcCallId() {
        return rpcCallId;
    }

    public void setRpcCallId(long rpcCallId) {
        this.rpcCallId = rpcCallId;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "RpcMessage{" +
                "rpcCallId=" + rpcCallId +
                ", success=" + success +
                ", body=" + body +
                '}';
    }
}
