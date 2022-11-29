package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.common.CustomException;
import com.buer.regi.dto.SetmealDto;
import com.buer.regi.entity.Setmeal;
import com.buer.regi.entity.SetmealDish;
import com.buer.regi.mapper.SetmealMapper;
import com.buer.regi.service.SetmealDishService;
import com.buer.regi.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息，操作Setmeal表，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //用stream流给每个setmealDish赋上套餐id
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作Setmeal_dish表，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
        
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);

        if (count > 0){
            //不能删除，抛出业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);

        //操作Setmeal_dish表
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }
}
