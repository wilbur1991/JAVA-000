/*******************************************************
 * Copyright (C) 2020 demo.COM - All Rights Reserved
 *
 * This file is part of sharding-sphere-demo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-13
 * @Author jiangwenbo <demo>
 *
 *******************************************************/

package com.wilbur.shardingspheredemo.service;

import com.wilbur.shardingspheredemo.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderItemService {

    List<OrderItem> selectTopTen();
    List<OrderItem> selectInTransaction();

    List<OrderItem> insertAndSelect();

    List<OrderItem> insertAndSelectInTransaction();
}
