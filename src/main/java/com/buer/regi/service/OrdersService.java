package com.buer.regi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buer.regi.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
