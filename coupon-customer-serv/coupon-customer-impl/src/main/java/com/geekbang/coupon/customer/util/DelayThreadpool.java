package com.geekbang.coupon.customer.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class DelayThreadpool {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(200);;
        taskExecutor.setKeepAliveSeconds(20);
        taskExecutor.setThreadFactory(new ThreadFactory() {
            AtomicInteger i = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable runnable) {
                Thread t = new Thread(runnable);
                t.setName("DelayThread:"+i.toString());
                i.incrementAndGet();
                return t;
            }
        });
        return taskExecutor;
    }
}
