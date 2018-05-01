package com.rdc.proxy;

import com.rdc.bootstrap.Registrant;
import com.rdc.bootstrap.Router;
import com.rdc.connection.ConnectionCenter;
import com.rdc.loadbalance.LoadBalanceStrategy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author SD
 * @since 2018/5/1
 */
public class CglibProxyInvoker<I> implements Supplier<I> {

    private I proxyInstance;

    private RpcMethodInvoker rpcMethodInvoker;

    @SuppressWarnings("unchecked")
    CglibProxyInvoker(Class<I> clazz,
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

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> rpcMethodInvoker.invoke(methodProxy, method, objects));
        this.proxyInstance = (I) enhancer.create();
    }

    @Override
    public I get() {
        return proxyInstance;
    }
}
