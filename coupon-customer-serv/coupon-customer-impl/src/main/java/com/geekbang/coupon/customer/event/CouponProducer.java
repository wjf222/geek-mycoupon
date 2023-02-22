package com.geekbang.coupon.customer.event;

import com.geekbang.coupon.customer.api.beans.RequestCoupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
public class CouponProducer {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private Executor delayExecutor;

    DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();
    public void sendCoupon(RequestCoupon coupon) {
        log.info("sent: {}", coupon);
        streamBridge.send(EventConstant.ADD_COUPON_EVENT, coupon);
    }

    // 使用延迟消息发送
    public void sendCouponInDelay(RequestCoupon coupon) {
        log.info("sent: {}", coupon);
        Long runAt = System.currentTimeMillis()+1000;
        DelayedTask<RequestCoupon> task = new DelayedTask<>(coupon,runAt);
        delayQueue.add(task);
    }

    @Async
    public void sendMessage() throws InterruptedException {
        while(true) {
            DelayedTask delayedTask = delayQueue.poll();
            // 这里应该并不需要线程池，先用着吧
            if(delayedTask != null) {
                delayExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        streamBridge.send(EventConstant.ADD_COUPON_DELAY_EVENT,
                                MessageBuilder.withPayload(delayedTask.data)
                                        .build());
                    }
                });
            } else {
                // 如果没有延时消息，就休息
                TimeUnit.SECONDS.sleep(5);
            }
        }

    }
    public void deleteCoupon(Long userId, Long couponId) {
        log.info("sent delete coupon event: userId={}, couponId={}", userId, couponId);
        streamBridge.send(EventConstant.DELETE_COUPON_EVENT, userId + "," + couponId);
    }

    private static class DelayedTask<T> implements Delayed {
        private final T data;
        private final Long runAt;

        private DelayedTask(T data, Long runAt) {
            this.data = data;
            this.runAt = runAt;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(this.runAt-System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            DelayedTask obj = (DelayedTask) delayed;
            return this.runAt.compareTo(obj.runAt);
        }
    }
}
