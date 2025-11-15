package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单细节
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据订单查询订单详细信息
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    /**
     * 统计不同状态的订单数量
     * @param statusValue
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    int getCountByStatus(int status);
}
