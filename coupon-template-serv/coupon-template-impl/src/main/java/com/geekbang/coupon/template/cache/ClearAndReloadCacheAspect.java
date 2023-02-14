package com.geekbang.coupon.template.cache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class ClearAndReloadCacheAspect {
    @Autowired
    private RedisService redisService;

    @Pointcut("@annotation(com.geekbang.coupon.template.cache.ClearAndReloadCache)")
    public void pointCut() {}

    @Around("pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint){
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        ClearAndReloadCache annotation = targetMethod.getAnnotation(ClearAndReloadCache.class);//反射得到自定义注解的方法对象
        String name = annotation.name();
        redisService.del(name);
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //开一个线程 延迟1秒（此处是1秒举例，可以改成自己的业务）
        // 在线程中延迟删除  同时将业务代码的结果返回 这样不影响业务代码的执行
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                redisService.del(name);
                System.out.println("-----------1秒钟后，在线程中延迟删除完毕 -----------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return proceed;
    }
}
