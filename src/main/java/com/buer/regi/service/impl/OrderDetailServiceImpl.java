package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.entity.OrderDetail;
import com.buer.regi.mapper.OrderDetailMapper;
import com.buer.regi.mapper.OrdersMapper;
import com.buer.regi.service.OrderDetailService;
import com.buer.regi.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
