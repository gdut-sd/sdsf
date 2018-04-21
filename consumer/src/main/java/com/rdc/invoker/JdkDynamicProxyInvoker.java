package com.rdc.invoker;

import com.rdc.bootstrap.Router;
import com.rdc.connection.ConnectionCenter;
import com.rdc.model.RpcCallRequest;
import com.rdc.model.RpcMessage;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author SD
 */
public class JdkDynamicProxyInvoker<I> implements InvocationHandler, Supplier<I> {

    private final ConnectionCenter connectionCenter;

    private Class<I> ic;

    private String version;

    private I proxyInstance;

    public JdkDynamicProxyInvoker(ConnectionCenter connectionCenter, Class<I> ic, String version) {
        this.connectionCenter = connectionCenter;
        this.ic = ic;
        this.version = version;
        Class<I>[] ics = new Class[]{ic};
        proxyInstance = (I) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), ics, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // as rpc : remote invoke

        // RPC：
        // 研究背景
        // 系统功能前景
        // 业界发展状况
        // 实现方案

        // progress:

        // 1. build rpc request
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setSessionId(1001);
        rpcMessage.setRpcCallId(1001); // TODO use an ID generator, may be snowflake

        RpcCallRequest request = new RpcCallRequest();
        request.setInterfaceName(ic.getName());
        request.setMethodName(method.getName());
        request.setParamterTypes(method.getParameterTypes());
        request.setArgs(args);
        request.setVersion(version); // FIXME set version
        request.setRpcCallId(rpcMessage.getRpcCallId());

        rpcMessage.setBody(request);

        // 2. send rpc request and wait for result(Future#get)
        Router router = connectionCenter.newRouter();
        Future<Object> result = router.send(request.getInterfaceName(), request.getVersion(), rpcMessage);

        // 3. return result
        try {
            return result.get(5000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Override
    public I get() {
        return proxyInstance;
    }
}
