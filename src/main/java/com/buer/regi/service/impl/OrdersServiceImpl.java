package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.common.BaseContext;
import com.buer.regi.common.CustomException;
import com.buer.regi.entity.*;
import com.buer.regi.mapper.OrdersMapper;
import com.buer.regi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {

        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        if (list == null || list.size() ==0){
            throw new CustomException("购物车为空，不能下单");
        }

        User user = userService.getById(currentId);

        Long addressBookId = orders.getAddressBookId();

        AddressBook addressBook = addressBookService.getById(addressBookId);

        if (addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();  //订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = list.stream().map((item) -> {

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;

        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        orderDetailService.saveBatch(orderDetails);

        shoppingCartService.remove(queryWrapper);
    }

























}
