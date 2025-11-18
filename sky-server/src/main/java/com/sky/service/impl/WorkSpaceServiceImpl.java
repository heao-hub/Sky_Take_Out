package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 查询指定日期范围内营业数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin,LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        // 全部订单数
        Integer totalOrders = orderMapper.getOrderCountByMap(map);

        map.put("status", Orders.COMPLETED);
        // 营业额
        Double turnoverByMap = orderMapper.getTurnoverByMap(map);
        turnoverByMap = turnoverByMap == null? 0.0: turnoverByMap;

        // 有效订单数
        Integer completedOrders = orderMapper.getOrderCountByMap(map);
        // 平均客单价
        double unitPrice = 0.0;
        // 订单完成率
        double orderCompletionRate = 0.0;
        if(totalOrders != 0 && completedOrders != 0){
            unitPrice = turnoverByMap / completedOrders;
            orderCompletionRate = completedOrders.doubleValue() / totalOrders;
        }

        // 新增用户数
        Integer newUsers = userMapper.getCountByMap(map);


        return BusinessDataVO
                .builder()
                .turnover(turnoverByMap)
                .validOrderCount(completedOrders)
                .unitPrice(unitPrice)
                .orderCompletionRate(orderCompletionRate)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单总览数据
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView(LocalDateTime begin,LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);

        // 查询当日总订单数
        Integer totalOrders = orderMapper.getOrderCountByMap(map);
        // 已取消订单
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.getOrderCountByMap(map);
        // 已完成订单
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.getOrderCountByMap(map);
        // 待派送订单
        map.put("status",Orders.CONFIRMED);
        Integer confirmedOrders = orderMapper.getOrderCountByMap(map);
        // 待接单订单
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmedOrders = orderMapper.getOrderCountByMap(map);

        return OrderOverViewVO
                .builder()
                .allOrders(totalOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(confirmedOrders)
                .waitingOrders(toBeConfirmedOrders)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getDishOverView() {

        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        // 在售菜品
        Integer onSaleDishes = dishMapper.getCountByMap(map);
        // 停售菜品
        map.put("status",StatusConstant.DISABLE);
        Integer offSaleDishes = dishMapper.getCountByMap(map);

        return DishOverViewVO
                .builder()
                .sold(onSaleDishes)
                .discontinued(offSaleDishes)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        // 在售套餐
        Integer onSaleSetmeal = setmealMapper.getCountByMap(map);
        // 停售套餐
        map.put("status",StatusConstant.DISABLE);
        Integer offSaleSetmeal = setmealMapper.getCountByMap(map);
        return SetmealOverViewVO
                .builder()
                .sold(onSaleSetmeal)
                .discontinued(offSaleSetmeal)
                .build();
    }
}
