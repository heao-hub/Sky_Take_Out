package com.sky.task;

import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时处理超时未支付订单
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder(){
        log.info("定时处理超时未支付订单，{}",LocalDateTime.now());

        // 支付的有效时间是15min，超过15min就将用户订单取消
        // 获取当前时间 - 15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // 只有订单状态为待支付的订单
        // 两个参数：未支付状态、当前时间-15min
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT,time);

        for (Orders orders : ordersList) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("订单超时未支付，自动取消");
            orderMapper.update(orders);
        }
    }

    /**
     * 定时处理一直处于派送中的订单
     * 每天凌晨一点处理，上一天还在配送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder(){
        log.info("定时处理一直派送中的订单，{}",LocalDateTime.now());

        // 执行方法的时间是每天凌晨一点，这样当前时间-60min就是前一天
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        // 查询状态为派送中的、前一天的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        for (Orders orders : ordersList) {
            // 如果预计送达时间比当前时间大/晚，那么表示用户是刚下单的，不能直接将订单修改为已完成
            // 因此只能修改那些预计送达时间比当前时间小/早的
            if(orders.getEstimatedDeliveryTime().compareTo(LocalDateTime.now()) < 0){
                // 将订单全部改为已完成
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }
}
