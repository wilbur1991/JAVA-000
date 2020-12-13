package com.wilbur.shardingspheredemo.repository;

import com.wilbur.shardingspheredemo.model.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("select * from order_item order by id desc limit 10")
    List<OrderItem> selectTopTen();

    @Insert(" INSERT INTO `order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`) " +
            " VALUES (#{orderId},#{commodityId},#{num},#{originPrice},#{actualPrice})")
    Long insert(OrderItem orderItem);
}
