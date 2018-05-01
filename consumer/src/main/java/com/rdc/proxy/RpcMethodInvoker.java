package com.rdc.proxy;

import com.rdc.bootstrap.Router;
import com.rdc.model.RpcCallRequest;
import com.rdc.model.RpcMessage;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author SD
 * @since 2018/5/1
 */
class RpcMethodInvoker {
    private String interfaceName;

    private String version;

    private Router router;

    private int autoRetryTimes;

    private int timeoutMillis;

    public RpcMethodInvoker(String interfaceName, String version, Router router, int autoRetryTimes, int timeoutMillis) {
        this.interfaceName = interfaceName;
        this.version = version;
        this.router = router;
        this.autoRetryTimes = autoRetryTimes;
        this.timeoutMillis = timeoutMillis;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // RPC：
        // 研究背景
        // 系统功能前景
        // 业界发展状况
        // 实现方案

        // 1. build rpc request
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setSessionId(1001);
        rpcMessage.setRpcCallId(1001); // TODO use an ID generator, may be snowflake

        RpcCallRequest request = new RpcCallRequest();
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArgs(args);
        request.setVersion(version);
        request.setRpcCallId(rpcMessage.getRpcCallId());

        rpcMessage.setBody(request);

        // 2. send rpc request and wait for result(Future#get)

        // 3. return result
        for (int i = 0; i < autoRetryTimes; i++) {
            try {
                send(request.getInterfaceName(), request.getVersion(), rpcMessage);
            } catch (Exception e) {
                // ignore and retry
            }
        }

        // do not retry any more
        return send(request.getInterfaceName(), request.getVersion(), rpcMessage);
    }

    private Object send(String interfaceName, String version, RpcMessage rpcMessage) throws Exception {
        Future<Object> result = router.send(interfaceName, version, rpcMessage);
        return result.get(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
