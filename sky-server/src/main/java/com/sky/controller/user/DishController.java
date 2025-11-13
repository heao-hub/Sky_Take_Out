package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Slf4j
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> listByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品，{}",categoryId);

        // 构造redis中存放菜品数据的key 规则：“dish_"+分类id
        String key = "dish_"+categoryId;
        // 查询redis中 是否存在菜品数据
        List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);

        if(dishes != null && dishes.size()>0){
            // 如果存在，直接将redis中的数据返回
            return Result.success(dishes);
        }

        //如果不存在，查询数据库
        dishes = dishService.listWithFlavorByCategoryId(categoryId);
        //将查询到的数据写入redis中
        redisTemplate.opsForValue().set(key,dishes);

        return Result.success(dishes);
    }
}
