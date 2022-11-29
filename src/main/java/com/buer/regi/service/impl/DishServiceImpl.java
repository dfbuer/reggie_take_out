package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.dto.DishDto;
import com.buer.regi.entity.Dish;
import com.buer.regi.entity.DishFlavor;
import com.buer.regi.mapper.DishMapper;
import com.buer.regi.service.DishFlavorService;
import com.buer.regi.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存口味数据
     * @param dishDto
     */
    @Override
   //加入事务注解
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> dishFlavors = dishDto.getFlavors();


        dishFlavors = dishFlavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //for (DishFlavor dishFlavor : dishFlavors) {
        //    dishFlavor.setDishId(dishId);
        //}
        //保存菜品口味数据到菜品表dish_flavor

        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品信息，从dish表查询
        Dish dish = this.getById(id);//this谁调用就是谁
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应的口味信息，从dish_flavor表查询
        //DishFlavor dishFlavor = dishFlavorService.getById(dishId);//因为可能有多个口味，所以不能这样直接查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //更新dish表信息
        this.updateById(dishDto);

        //清理当前菜品对应的口味数据，针对dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据,针对dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
