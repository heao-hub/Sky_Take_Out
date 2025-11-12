package com.sky.controller.user;


import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@Slf4j
@Api(tags = "C端-分类相关接口")
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据类型查询分类信息
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类信息")
    public Result<List<Category>> listByType(Integer type){
        log.info("C端-根据类型查询分类信息，{}",type);

        List<Category> categories = categoryService.listByType(type);

        return Result.success(categories);
    }
}
