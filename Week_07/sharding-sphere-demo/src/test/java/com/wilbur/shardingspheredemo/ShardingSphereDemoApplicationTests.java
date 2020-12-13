package com.wilbur.shardingspheredemo;

import com.wilbur.shardingspheredemo.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShardingSphereDemoApplicationTests {

	@Autowired
	private OrderItemService orderItemService;
	@Test
	void testReadOnly() {
		System.out.println(orderItemService.selectTopTen().toString());
	}
	@Test
	void testSelectInTransaction() {
		System.out.println(orderItemService.selectInTransaction().toString());
	}

	@Test
	void testWriteAndRead() {
		System.out.println(orderItemService.insertAndSelect().toString());
	}

	@Test
	void testWriteAndReadInTransaction() {
		System.out.println(orderItemService.insertAndSelectInTransaction().toString());
	}
}
