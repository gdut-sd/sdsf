package com.rdc.proxy;

import com.rdc.bootstrap.Registrant;
import com.rdc.bootstrap.Router;
import com.rdc.connection.ConnectionCenter;
import com.rdc.loadbalance.LoadBalanceStrategy;
import com.rdc.model.RpcCallRequest;
import com.rdc.model.RpcMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author SD
 */
public class JdkDynamicProxyInvoker<I> implements Supplier<I> {

    private I proxyInstance;

    private RpcMethodInvoker rpcMethodInvoker;

    @SuppressWarnings("unchecked")
    public JdkDynamicProxyInvoker(Class<I> clazz,
                                  String version,
                                  ConnectionCenter connectionCenter,
                                  Registrant registrant,
                                  LoadBalanceStrategy loadBalanceStrategy,
                                  int autoRetryTimes,
                                  int timeoutMillis) {
        if (clazz == null) {
            throw new IllegalArgumentException("service class should not be null.");
        }
        if (connectionCenter == null) {
            throw new IllegalArgumentException("connection center should not be null.");
        }
        if (registrant == null) {
            throw new IllegalArgumentException("registrant should not be null.");
        }
        if (autoRetryTimes < 0 || autoRetryTimes > 100) {
            throw new IllegalArgumentException("auto retry time should be within 0 and 100");
        }
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeout should not be negative.");
        }

        Router router = new Router(connectionCenter, registrant, loadBalanceStrategy);
        this.rpcMethodInvoker = new RpcMethodInvoker(clazz.getName(), version, router, autoRetryTimes, timeoutMillis);

        Class<I>[] ics = new Class[]{clazz};
        proxyInstance = (I) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), ics, rpcMethodInvoker::invoke);
    }

    @Override
    public I get() {
        return proxyInstance;
    }
}
