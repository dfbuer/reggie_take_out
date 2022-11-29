package com.buer.regi.controller;

import com.buer.regi.common.R;
import com.buer.regi.entity.Orders;
import com.buer.regi.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        log.info("订单信息 = {}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }


}
