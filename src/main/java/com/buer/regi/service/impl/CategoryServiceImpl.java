package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.common.CustomException;
import com.buer.regi.entity.Category;
import com.buer.regi.entity.Dish;
import com.buer.regi.entity.Setmeal;
import com.buer.regi.mapper.CategoryMapper;
import com.buer.regi.service.CategoryService;
import com.buer.regi.service.DishService;
import com.buer.regi.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据id分类进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count > 0){
            //关联了菜品，抛出业务异常
            throw new CustomException("当前分类下关联了关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        if (count > 0){
            //关联了套餐，抛出业务异常
            throw new CustomException("当前分类下关联了关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
