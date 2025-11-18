package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

  /**
     * 条件分页查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQueryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 查询超时未支付订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time} ")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime time);

    /**
     * 统计不同状态的订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    int getCountByStatus(int status);

    /**
     * 营业额统计
     * @param map
     * @return
     */
    Double getTurnoverByMap(Map<String, Object> map);

    /**
     * 订单数统计
     * @param map
     * @return
     */
    Integer getOrderCountByMap(Map<String, Object> map);

    /**
     * 查询销量前十的菜品
     * @param map
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(Map<String, Object> map);
}
