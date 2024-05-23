package com.myclass.agent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

@Slf4j
public class DemoInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {
        String methodName = method.getName();
        log.info("Call method: {}", methodName);
        long start = System.nanoTime();
        Object result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            log.error("Call method: {} error, msg: {}", methodName, e.getMessage());
        }
        long end = System.nanoTime();
        log.info("Call method: {}, cost time: {} ns", methodName, end - start);
        return result;
    }
}
