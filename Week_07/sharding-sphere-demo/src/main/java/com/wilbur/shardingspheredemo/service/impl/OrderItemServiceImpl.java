/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of sharding-sphere-demo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-13
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package com.wilbur.shardingspheredemo.service.impl;


import com.wilbur.shardingspheredemo.model.OrderItem;
import com.wilbur.shardingspheredemo.repository.OrderItemMapper;
import com.wilbur.shardingspheredemo.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> selectTopTen() {
        return orderItemMapper.selectTopTen();
    }

    @Override
    @Transactional
    public List<OrderItem> selectInTransaction() {
        return orderItemMapper.selectTopTen();
    }

    @Override
    public List<OrderItem> insertAndSelect() {

        orderItemMapper.insert(generateOrderItem());
        return orderItemMapper.selectTopTen();
    }

    @Override
    @Transactional
    public List<OrderItem> insertAndSelectInTransaction() {
        orderItemMapper.insert(generateOrderItem());
        return orderItemMapper.selectTopTen();
    }

    private OrderItem generateOrderItem(){
        OrderItem orderItem = new OrderItem();
        double price = ThreadLocalRandom.current().nextInt(100) + ThreadLocalRandom.current().nextDouble();
        orderItem.setOrderId(ThreadLocalRandom.current().nextLong(10000));
        orderItem.setCommodityId(ThreadLocalRandom.current().nextLong(10000));
        orderItem.setNum(1);
        orderItem.setOriginPrice(price);
        orderItem.setOriginPrice(price*0.8);
        return orderItem;
    }


}
