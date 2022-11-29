package com.buer.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buer.regi.common.R;
import com.buer.regi.dto.DishDto;
import com.buer.regi.entity.Category;
import com.buer.regi.entity.Dish;
import com.buer.regi.entity.DishFlavor;
import com.buer.regi.service.CategoryService;
import com.buer.regi.service.DishFlavorService;
import com.buer.regi.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDto = {}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 菜品查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> get(int page, int pageSize,String name){

        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        Page<Dish> page1 = dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();


        List<DishDto> list = records.stream().map((item -> {
            DishDto dishDto = new DishDto();

            //将其他属性拷贝到dishDto对象中
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//菜品分类id
            Category category = categoryService.getById(categoryId);//根据id查询分类对象

            if (category != null){
                String categoryName = category.getName();//拿到分类的名称
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        })).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 查询菜品，在修改时进行回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    //@GetMapping("/list")
    //public R<List<Dish>> list(Dish dish){
    //
    //    //构造查询
    //    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //    //构造查询条件
    //    queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
    //    //查询状态是否起售
    //    queryWrapper.eq(Dish::getStatus,1);
    //    //添加排序条件
    //    queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
    //    //查询
    //    List<Dish> list = dishService.list(queryWrapper);
    //
    //    return R.success(list);
    //}

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        //构造查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态是否起售
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        //查询
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long id = item.getId();

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();

            queryWrapper1.eq(id != null,DishFlavor::getDishId,id);

            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(dishFlavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


}
