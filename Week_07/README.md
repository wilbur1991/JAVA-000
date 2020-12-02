

## 2.（必做）**按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率

### a 最原始的方式每次插入一条，耗时如下：

total insert time in 321138 ms

### b 使用批处理方式，每次插入一万条，耗时如下：

total insert time in 10929 ms

### c 调整max_allowed_packet，每次插入10w条，耗时如下：

total insert time in 13807 ms

### d 使用prepareStatement

total insert time in 107461 ms

### e 使用连接池

total insert time in 8290 ms



