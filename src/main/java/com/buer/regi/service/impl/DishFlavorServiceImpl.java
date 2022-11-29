package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.entity.DishFlavor;
import com.buer.regi.mapper.DishFlavorMapper;
import com.buer.regi.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
