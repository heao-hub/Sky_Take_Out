package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@Slf4j
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 查询当天营业数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("查询当天营业数据")
    public Result<BusinessDataVO> businessData(){
        log.info("查询当天营业数据");
        LocalDate date = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(begin,end);
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单总览数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单总览数据")
    public Result<OrderOverViewVO> orderOverView(){
        log.info("查询订单总览数据");
        LocalDate date = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
        OrderOverViewVO orderOverViewVO = workSpaceService.getOrderOverView(begin,end);
        return Result.success(orderOverViewVO);
    }

    /**
     * 菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("菜品总览")
    public Result<DishOverViewVO> dishOverview(){
        log.info("菜品总览");
        DishOverViewVO dishOverViewVO = workSpaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> setmealOverView(){
        log.info("查询套餐总览");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}
