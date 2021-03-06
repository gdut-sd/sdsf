package com.rdc.model;

import java.io.Serializable;

/**
 * @author SD
 */
public class RpcCallRequest implements Serializable {

    private static final long serialVersionUID = -7018564416757153621L;

    private long rpcCallId;

    private String interfaceName;

    private String methodName;

    private String version;

    private Class<?>[] paramterTypes;

    private Object[] args;

    public RpcCallRequest() {
    }

    public RpcCallRequest(long rpcCallId, String interfaceName, String methodName, String version, Class<?>[] paramterTypes, Object[] args) {
        this.rpcCallId = rpcCallId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.version = version;
        this.paramterTypes = paramterTypes;
        this.args = args;
    }

    public long getRpcCallId() {
        return rpcCallId;
    }

    public void setRpcCallId(long rpcCallId) {
        this.rpcCallId = rpcCallId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class<?>[] getParamterTypes() {
        return paramterTypes;
    }

    public void setParamterTypes(Class<?>[] paramterTypes) {
        this.paramterTypes = paramterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
