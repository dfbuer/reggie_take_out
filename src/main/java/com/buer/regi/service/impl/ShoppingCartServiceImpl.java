package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.entity.ShoppingCart;
import com.buer.regi.mapper.ShoppingCartMapper;
import com.buer.regi.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
