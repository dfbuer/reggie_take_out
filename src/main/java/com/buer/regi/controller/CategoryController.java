package com.buer.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buer.regi.common.R;
import com.buer.regi.entity.Category;
import com.buer.regi.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询步骤
     * 1.页面发送ajax请求，将分页查询参数（page,pageSize,name）提交到服务端
     * 2.服务端Controller接受页面提交的数据并调用Service查询数据
     * 3.Service调用Mapper操作数据库，查询分页数据
     * 4.Controller将查询到的分页数据相应给页面
     * 5.页面接收到分页数据并通过ElementUI的Table组件展示到页面上
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);
        //构造分页构造器
        Page<Category> page1 = new Page<>(page,pageSize);
        //构造条件查询
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，按照sort排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(page1,lambdaQueryWrapper);

        return R.success(page1);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为：{}",ids);

        //categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //构造查询条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
