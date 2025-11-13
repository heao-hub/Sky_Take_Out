package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前新添加的菜品或者套餐是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        // 如果已经存在，那么将商品数量number+1即可
        if(list != null && list.size()>0){
            // 由于上面的查询 其查询结果只可能有一个即只会有一个菜品或者套餐，所以list集合中只有一个元素
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart);

        }else{
            // 如果不存在，那么将商品新增到购物车中
            if(shoppingCart.getDishId() != null){
                // 当前添加的是菜品
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                // 当前添加的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart);
        }



    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 查询该商品
        ShoppingCart shoppingCart = new ShoppingCart();;
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        // 如果没有查到，抛出异常
        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);
            Integer number = shoppingCart.getNumber();
            // 如果当前商品只有一份，那么直接删除
            if(number == 1){
                shoppingCartMapper.delete(shoppingCart);
            }else if(number > 1){
                // 如果当前商品大于1份，那么将number-1
                number = number - 1;
                shoppingCart.setNumber(number);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }else{
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPINGCART_NOT_EXISTS);
        }
    }
}
