package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 套餐数据层
 */
@Mapper
public interface SetMealMapper {
    /**
     * 根据分类id查询该分类下套餐数量
     * @param categoryId
     * @return
     */
    @Select("select count(*) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
