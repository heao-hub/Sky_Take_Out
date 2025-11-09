package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setMealMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setStatus(StatusConstant.DISABLE);
        /*category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
*/
        categoryMapper.insert(category);

    }

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        Page<Category> result = categoryMapper.pageQuery(categoryPageQueryDTO);
        Long total = result.getTotal();
        List<Category> records = result.getResult();


        return new PageResult(total,records);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        /*category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());*/

        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        //查询当前分类下是否关联了菜品，如果关联了就抛出异常
        Integer dish = dishMapper.countByCategoryId(id);
        if (dish > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类下是否关联了套餐，如果有，就抛出异常
        Integer setMeal = setMealMapper.countByCategoryId(id);
        if (setMeal > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }


        categoryMapper.delete(id);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> listByType(Integer type) {

        List<Category> records = categoryMapper.listByType(type);

        return records;
    }

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        /*category.setUpdateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
*/
        categoryMapper.update(category);
    }

    /**
     * 查询所有分类
     * @return
     */
    @Override
    public List<Category> list() {
        List<Category> records = categoryMapper.list();
        return records;
    }
}
