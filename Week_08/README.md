## 2、（必做）设计对前面的订单表数据进行水平分库分表，拆分2个库，每个库16张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到Github。

使用sharding-proxy实现分库分表，conf-sharding配置如下：

```yaml

schemaName: sharding_db
dataSourceCommon:
 username: one_dev
 password: one_dev
 connectionTimeoutMilliseconds: 30000
 idleTimeoutMilliseconds: 60000
 maxLifetimeMilliseconds: 1800000
 maxPoolSize: 50
 minPoolSize: 1
 maintenanceIntervalMilliseconds: 30000

dataSources:
 ds_0:
   url: jdbc:mysql://127.0.0.1:3306/java_camp_demo_a?serverTimezone=UTC&useSSL=false
 ds_1:
   url: jdbc:mysql://127.0.0.1:3306/java_camp_demo_b?serverTimezone=UTC&useSSL=false

rules:
- !SHARDING
 tables:
   t_order:
     actualDataNodes: ds_${0..1}.t_order_${0..15}
     tableStrategy:
       standard:
         shardingColumn: order_id
         shardingAlgorithmName: t_order_inline
     keyGenerateStrategy:
       column: order_id
       keyGeneratorName: snowflake
   t_order_item:
     actualDataNodes: ds_${0..1}.t_order_item_${0..15}
     tableStrategy:
       standard:
         shardingColumn: order_id
         shardingAlgorithmName: t_order_item_inline
     keyGenerateStrategy:
       column: order_item_id
       keyGeneratorName: snowflake
 bindingTables:
   - t_order,t_order_item
 defaultDatabaseStrategy:
   standard:
     shardingColumn: user_id
     shardingAlgorithmName: database_inline
 defaultTableStrategy:
   none:
 
 shardingAlgorithms:
   database_inline:
     type: INLINE
     props:
       algorithm-expression: ds_${user_id % 2}
   t_order_inline:
     type: INLINE
     props:
       algorithm-expression: t_order_${order_id % 16}
   t_order_item_inline:
     type: INLINE
     props:
       algorithm-expression: t_order_item_${order_id % 16}
 
 keyGenerators:
   snowflake:
     type: SNOWFLAKE
     props:
       worker-id: 123

```

server.yaml配置如下：

```yaml

authentication:
 users:
   one_dev:
     password: one_dev
   sharding:
     password: sharding 
     authorizedSchemas: sharding_db

props:
 max-connections-size-per-query: 1
 acceptor-size: 16  # The default value is available processors count * 2.
 executor-size: 16  # Infinite by default.
 proxy-frontend-flush-threshold: 128  # The default value is 128.
   # LOCAL: Proxy will run with LOCAL transaction.
   # XA: Proxy will run with XA transaction.
   # BASE: Proxy will run with B.A.S.E transaction.
 proxy-transaction-type: LOCAL
 proxy-opentracing-enabled: false
 proxy-hint-enabled: false
 query-with-cipher-column: true
 sql-show: true
 check-table-metadata-enabled: false
```

启动shardingsphere-proxy:

```shell
sh bin/start.sh
```

通过shardingsphere-proxy连接至mysql，默认端口3307：(连接时host不可使用localhost)

```
mysql -u one_dev  -pone_dev -h 127.0.0.1 -P 3307
```

创建表结构，自动生成分表：

```mysql
mysql> CREATE TABLE IF NOT EXISTS `t_order` (   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',   `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',   `order_id` bigint(20) DEFAULT NULL COMMENT '用户ID',   `total_amount` decimal(10,0) DEFAULT NULL COMMENT '总金额',   `actual_amount` decimal(10,0) DEFAULT NULL COMMENT '实付金额',   `date_created` datetime DEFAULT NULL COMMENT '创建时间',   `status` tinyint(4) DEFAULT NULL COMMENT '订单状态：0-待支付，1-已支付，2-已取消',   `deliver_status` tinyint(4) DEFAULT NULL COMMENT '订单状态：0-初始状态，1-待配送，2-配送中，3配送成功，4已完成',   `last_updated` datetime DEFAULT NULL COMMENT '最近更新时间',   PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
Query OK, 0 rows affected (1.37 sec)

mysql> CREATE TABLE IF NOT EXISTS `t_order_item` (
    ->   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ->   `order_id` bigint(20) unsigned DEFAULT NULL COMMENT '订单ID',
    ->   `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    ->   `commodity_id` bigint(20) unsigned DEFAULT NULL COMMENT '商品ID',
    ->   `num` int(10) DEFAULT NULL COMMENT '购买商品数量',
    ->   `origin_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
    ->   `actual_price` decimal(10,2) DEFAULT NULL COMMENT '商品实际价格',
    ->   PRIMARY KEY (`id`)
    -> ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
Query OK, 0 rows affected (0.96 sec)

```

使用如下程序插入100万条数据，直接拼接insert语句，每次插入一万条数据，耗时79660ms，并查看数据分布情况：

```java


import java.sql.*;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;


public class JDBCDemo {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3307/sharding_db?serverTimezone=UTC";

    // Database credentials
    static final String USER = "one_dev";
    static final String PASS = "one_dev";

    static final String SQL = "INSERT INTO `t_order_item`( `order_id`, `user_id`,`commodity_id`, `num`, `origin_price`, `actual_price`) VALUES ";

    static final String VALUE_FORMAT = "( %d,%d, %d, %d, %f, %f)";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        long start = System.currentTimeMillis();
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            stmt = conn.createStatement();
            Random random = new Random(17);
            int step = 10000,total = 1000000;

            for (int i = 0; i < total/step; i++) {
                StringJoiner stringJoiner = new StringJoiner(",");
                for (int j = 1; j <= step; j++) {
                    float price = random.nextInt(100)+random.nextFloat();
                    stringJoiner.add(String.format(VALUE_FORMAT, i*step  + j, ThreadLocalRandom.current().nextLong(10000), i*step  + j, 1, price, price * 0.8));
                }
                stmt.execute(SQL + stringJoiner.toString() + ";");
            }

        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            } // do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("total insert time in " + (end - start) + " ms");
        }
    }

}

```

由于日志量较大，随机截取几条日志信息看下实际插入时sql语句，可以看到values中order_id对16取模后与表名后缀一致。

```mysql
[INFO ] 18:11:45.176 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: INSERT INTO `t_order_item_14`( `order_id`, `user_id`,`commodity_id`, `num`, `origin_price`, `actual_price`, order_item_id) VALUES (990062, 4848, 990062, 1, 4.132352, 3.305882, 544943709322981437), (990126, 3584, 990126, 1, 8.247968, 6.598374, 544943709322981501), (990158, 9164, 990158, 1, 58.477158, 46.781726, 544943709322981533), (990190, 1482, 990190, 1, 77.887810, 62.310248, 544943709322981565), (990238, 1884, 990238, 1, 29.307295, 23.445836, 544943709322981613), (990270, 3020, 990270, 1, 52.694134, 42.155307, 544943709322981645), (990286, 8032, 990286, 1, 35.267517, 28.214014, 544943709322981661), (990302, 3288, 990302, 1, 31.230427, 24.984341, 544943709322981677), (990318, 8270, 990318, 1, 64.924026, 51.939221, 544943709322981693), (990334, 1692, 990334, 1, 31.040554, 24.832443, 544943709322981709), (990382, 778, 990382, 1, 42.100468, 33.680374, 544943709322981757), (990414, 2148, 990414, 1, 33.589397, 26.871518, 544943709322981789).......
[INFO ] 18:11:45.176 [ShardingSphere-Command-4] ShardingSphere-SQL - Actual SQL: ds_0 ::: INSERT INTO `t_order_item_0`( `order_id`, `user_id`,`commodity_id`, `num`, `origin_price`, `actual_price`, order_item_id) VALUES (990064, 5000, 990064, 1, 54.924191, 43.939352, 544943709322981439), (990144, 6020, 990144, 1, 69.233742, 55.386993, 544943709322981519), (990176, 8772, 990176, 1, 48.425545, 38.740436, 544943709322981551), (990192, 8932, 990192, 1, 75.035606, 60.028485, 544943709322981567), (990208, 7790, 990208, 1, 6.667952, 5.334362, 544943709322981583), (990224, 1754, 990224, 1, 64.463516, 51.570813, 544943709322981599), (990256, 1884, 990256, 1, 31.181929, 24.945543, 544943709322981631), (990272, 5032, 990272, 1, 9.461387, 7.569109, 544943709322981647), (990304, 1730, 990304, 1, 34.048286, 27.238629, 544943709322981679), (990320, 6220, 990320, 1, 46.494003, 37.195203, 544943709322981695), (990352, 4618, 990352, 1, 98.267464, 78.613971, 544943709322981727), (990368, 168, 990368, 1, 37.125565, 29.700452, 544943709322981743).......
```

插入结束后，查看数据总条数：

```mysql
mysql> select count(*) from t_order_item;
+----------+
| count(*) |
+----------+
|  1000000 |
+----------+
1 row in set (0.09 sec)

```

查看分表数据条数，数据分布均匀，表中数据量平均值接近1000000/32 = 31250。

![image-20201213183035279](/Users/wilbur/javaCamp/JAVA-000/Week_08/README.assets/image-20201213183035279.png)

![image-20201213183100489](/Users/wilbur/javaCamp/JAVA-000/Week_08/README.assets/image-20201213183100489.png)



## 2、（必做）基于hmily TCC或ShardingSphere的Atomikos XA实现一个简单的分布式事务应用demo（二选一），提交到github。

待完成