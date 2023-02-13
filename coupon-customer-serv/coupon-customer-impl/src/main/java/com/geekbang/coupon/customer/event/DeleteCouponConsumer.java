//package com.geekbang.coupon.customer.event;
//
//import com.geekbang.coupon.customer.service.intf.CouponCustomerService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//public class DeleteCouponConsumer {
//
//    @Autowired
//    private CouponCustomerService customerService;
//
//    @Bean
//    public Consumer<String> deleteCoupon() {
//        return request -> {
//            log.info("received: {}", request);
//            List<Long> params = Arrays.stream(request.split(","))
//                    .map(Long::valueOf)
//                    .collect(Collectors.toList());
//            customerService.deleteCoupon(params.get(0), params.get(1));
//        };
//    }
//
//}
