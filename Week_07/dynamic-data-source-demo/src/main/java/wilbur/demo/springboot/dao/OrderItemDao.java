/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-12
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package wilbur.demo.springboot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wilbur.demo.springboot.annotation.DynamicDataSource;
import wilbur.demo.springboot.model.Order;
import wilbur.demo.springboot.model.OrderItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class OrderItemDao {
    static final String SQL = "INSERT INTO `order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`) VALUES ";
    static final String VALUE_FORMAT = "( %d, %d, %d, %f, %f)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(OrderItem orderItem) {
        float price = ThreadLocalRandom.current().nextInt(100) + ThreadLocalRandom.current().nextFloat();
        String sql = SQL + String.format(VALUE_FORMAT, ThreadLocalRandom.current().nextInt(10000), ThreadLocalRandom.current().nextInt(10000), 1, price, price * 0.8);
        jdbcTemplate.execute(sql);
    }

    @DynamicDataSource(readOnly = true)
    public List<Map<String, Object>> select() {
        String sql = "select * from order_item order by id desc limit 10";
        return jdbcTemplate.queryForList(sql);
    }
}
