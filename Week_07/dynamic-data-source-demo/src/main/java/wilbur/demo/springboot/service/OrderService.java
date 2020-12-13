/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-10
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package wilbur.demo.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wilbur.demo.springboot.dao.OrderItemDao;
import wilbur.demo.springboot.model.OrderItem;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderItemDao orderItemDao;

    public void insert(OrderItem orderItem) {
        orderItemDao.insert(orderItem);
    }

    public List<Map<String, Object>> list() {
        return orderItemDao.select();
    }
}
