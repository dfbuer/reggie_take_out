package com.buer.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.buer.regi.common.BaseContext;
import com.buer.regi.common.R;
import com.buer.regi.entity.ShoppingCart;
import com.buer.regi.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将菜添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart){

        log.info("shoppingCart = {}",shoppingCart);

        Long currentId = BaseContext.getCurrentId();

        shoppingCart.setUserId(currentId);

        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);

        if (shoppingCart1 != null){
            //已经存在这个商品
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCart1);

        } else {
            //不存在这个商品
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }

        return R.success(shoppingCart1);
    }


    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("清除成功");
    }
}
