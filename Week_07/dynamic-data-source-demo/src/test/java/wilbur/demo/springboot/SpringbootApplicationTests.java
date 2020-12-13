package wilbur.demo.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wilbur.demo.springboot.model.OrderItem;
import wilbur.demo.springboot.service.OrderService;

import java.util.List;
import java.util.Map;

@SpringBootTest
class SpringbootApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void testDataSourceChange() {
        List<Map<String, Object>> result = orderService.list();
        System.out.println(result.toString());
        orderService.insert(new OrderItem());
        result = orderService.list();
        System.out.println(result.toString());
        result = orderService.list();
        System.out.println(result.toString());
    }
}
