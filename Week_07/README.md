

## 2.（必做）**按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率

下述实验每次插入数据前保证truncate清空表中数据，避免因表中数据量影响实验效果。详细源码见[springbootDemo](./springbootDemo)

### a 最原始的方式每次插入一条，主要插入代码如下：

```
    static final String SQL = "INSERT INTO `job`.`order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`) VALUES ";

    static final String VALUE_FORMAT = "( %d, %d, %d, %f, %f)";

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
            int step = 1,total = 1000000;

            for (int i = 0; i < total/step; i++) {
                StringJoiner stringJoiner = new StringJoiner(",");
                for (int j = 1; j <= step; j++) {
                    float price = random.nextInt(100)+random.nextFloat();
                    stringJoiner.add(String.format(VALUE_FORMAT, i*step  + j, i*step  + j, 1, price, price * 0.8));
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

```

这种方式下插入耗时如下，由于每次插入都需要执行insert语句，耗时主要在网络I/O:

``` 
total insert time in 321138 ms
```

### b 使用批处理方式，每次插入一万条，同上述代码，调整step为10000，耗时如下：

使用批处理方式，一共只需要执行insert语句100次，可以看到耗时有大幅度下降，那么是否进一步调大step，耗时会进一步降低呢。

``` 
total insert time in 10929 ms
```

### c 同a代码，step调整为100000，每次插入10w条，耗时如下：

每次插入10w条超过了默认max_allowed_packet大小，调整max_allowed_packet为10MB后，继续尝试耗时反而比 每次插入一万条要高。可见网络I/O已经不再是插入数据的瓶颈。

``` 
total insert time in 13807 ms
```

### d 使用prepareStatement

``` 
   static final String SQL = "INSERT INTO `job`.`order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`) VALUES (?,?,?,?,?)";


    public static void main(String[] args) {
        Connection conn = null;
        long start = System.currentTimeMillis();
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = getConn();
            conn.setAutoCommit(false);
            Random random = new Random(17);
            int step = 10000,total = 1000000;
            for (int i = 0; i < total/step; i++) {
                PreparedStatement preparedStatement = conn.prepareStatement(SQL);
                for (int j = 1; j <= step; j++) {
                    float price = random.nextInt(100) + random.nextFloat();
                    preparedStatement.setLong(1, i * step + j);
                    preparedStatement.setLong(2, i * step + j);
                    preparedStatement.setLong(3, 1);
                    preparedStatement.setDouble(4, price);
                    preparedStatement.setDouble(5, price * 0.8);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
            conn.commit();

        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
            //事务回滚
            try {
                if (Objects.nonNull(conn)) {
                    conn.rollback();
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }

        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //关闭连接
            try {
                conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("total insert time in " + (end - start) + " ms");
        }
    }

    private static Connection getConn() throws ClassNotFoundException, SQLException {
        // STEP 2: Register JDBC driver
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Connecting to a selected database...");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

```

尝试使用PreparedStatement批量插入数据，耗时比直接构建批量insert语句要差很多，时间有限具体原因并未进一步排查。

``` 
total insert time in 107461 ms
```

### e 程序生成主键ID，使用多线程及连接池进一步优化插入效率：

``` 
 static final String SQL = "INSERT INTO `job`.`order_item`(`id`, `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`) VALUES ";
    static final String VALUE_FORMAT = "( %d,%d, %d, %d, %f, %f)";

    public static void main(String[] args) {
        int step = 100000,total = 1000000;
        long start = System.currentTimeMillis();
        IntStream.range(0,total/step).parallel().forEach(x->{
            insert(x*step,step);
        });
        long end = System.currentTimeMillis();
        System.out.println("total insert time in " + (end - start) + " ms");
    }
    private static void insert(int index,int step) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = getConnFromPool();
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            insert(stmt,index,step);
            conn.commit();

        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
            //事务回滚
            try {
                if (Objects.nonNull(conn)) {
                    conn.rollback();
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }

        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
               }
    }

    private static void insert(Statement stmt,int index,int step) throws SQLException {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (int j = 1; j <= step; j++) {
            float price = ThreadLocalRandom.current().nextInt(100)+ThreadLocalRandom.current().nextFloat();
            stringJoiner.add(String.format(VALUE_FORMAT, index  + j,index  + j, index  + j, 1, price, price * 0.8));
        }
        stmt.execute(SQL + stringJoiner.toString() + ";");
    }

    /**
     * 从Hikari连接池获取连接
     * @return
     * @throws SQLException
     */
    private static Connection getConnFromPool() throws SQLException {
        return DataSourceUtil.getConnection();
    }

```

可以看到耗时进一步降低到10秒内，虽然同样批量插入10W条的实验d，耗时降低5517ms。在数据量更大的情况下性能提升应该会更加明显。

``` 
total insert time in 8290 ms
```



## 2.（必做）**读写分离 - 动态切换数据源版本 1.0

添加多数据源配置,一个支持读写的数据源writeDS，两个只读数据源readA，readB。基于DynamicDataSourceConfig构建JdbcTemplate。

``` java

@Configuration
public class DataSourceConfig {
    @Bean(name = DataSourceConstant.DEFAULT_DATASOURCE)
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "readA")
    @ConfigurationProperties("spring.datasource.read1")
    public DataSource readADataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "readB")
    @ConfigurationProperties("spring.datasource.read2")
    public DataSource readBDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DynamicDataSourceConfig getRoutingDataSourceConfig() {
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put("readA", readADataSource());
        targetDataSources.put("readB", readBDataSource());
        return new DynamicDataSourceConfig(writeDataSource(), targetDataSources);
    }

    @Bean("jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(getRoutingDataSourceConfig());
    }

}

```

继承AbstractRoutingDataSource，通过threadLocalDataSourceKey保存当前线程中使用的dataSource名称，通过重写determineCurrentLookupKey 实现动态数据源切换。

```java

public class DynamicDataSourceConfig extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> threadLocalDataSourceKey = new ThreadLocal<>();

    public DynamicDataSourceConfig(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public static void setDataSource(String dataSource) {
        threadLocalDataSourceKey.set(dataSource);
    }

    public static String getDataSource() {
        return threadLocalDataSourceKey.get();
    }

    public static void clearDataSource() {
        threadLocalDataSourceKey.remove();
    }

}

```

自定义注解DynamicDataSource,使用时如果想使用从库，需显示指定readOnly为true。

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicDataSource {
    boolean readOnly() default false;
}

```

使用AOP对使用了DynamicDataSource的dao层切换只读数据源。通过loadBalance方法随机获取只读数据源，需注意在切换数据源后需手动恢复到默认数据源。

```java
import static wilbur.demo.springboot.constant.DataSourceConstant.DEFAULT_DATASOURCE;

@Aspect
@Component
public class DynamicDataSourceAspect {

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    @Pointcut("@annotation(wilbur.demo.springboot.annotation.DynamicDataSource)")
    public void routingWith() {
    }

    @Around("routingWith() && @annotation(dynamicDataSource)")
    public Object routingWithDataSource(ProceedingJoinPoint joinPoint, DynamicDataSource dynamicDataSource) throws Throwable {
        if (dynamicDataSource.readOnly()) {
            final String slave = loadBalance();
            System.out.println("use readonly datasource " + slave);
            DynamicDataSourceConfig.setDataSource(slave);
        } else {
            System.out.println("use readonly datasource " + DEFAULT_DATASOURCE);
            DynamicDataSourceConfig.setDataSource(DEFAULT_DATASOURCE);
        }
        Object result = joinPoint.proceed();
        //执行结束后恢复默认数据源
        DynamicDataSourceConfig.setDataSource(DEFAULT_DATASOURCE);
        return result;
    }

		//随机获取只读数据源
    private String loadBalance() {
        Map<Object, DataSource> dataSourceMap = dynamicDataSourceConfig.getResolvedDataSources();
        List<Object> keys = new ArrayList<>(dataSourceMap.keySet()).stream().collect(Collectors.toList());
        final int i = ThreadLocalRandom.current().nextInt(keys.size());
        return keys.get(i).toString();
    }

}

```

OrderItemDao定义如下,使用JdbcTemplate实现简单数据库插入、读取操作。

```java

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

```

service层定义如下：

```java

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

```

创建测试类，模拟查询、插入、再查询的操作流程,执行结果符合预期。

```java

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

```



## 3.（必做）**读写分离 - 数据库框架版本 2.0

使用sharding-jdbc + mybatis实现读写分离。

sharding-jdbc配置如下：

```properties
sharding.jdbc.datasource.names=primary-ds,replica-ds-0,replica-ds-1

sharding.jdbc.datasource.primary-ds.jdbc-url=jdbc:mysql://localhost:3306/job?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
sharding.jdbc.datasource.primary-ds.type=com.zaxxer.hikari.HikariDataSource
sharding.jdbc.datasource.primary-ds.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.primary-ds.username=one_dev
sharding.jdbc.datasource.primary-ds.password=one_dev

sharding.jdbc.datasource.replica-ds-0.jdbc-url=jdbc:mysql://localhost:3306/java_camp_demo_a?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
sharding.jdbc.datasource.replica-ds-0.type=com.zaxxer.hikari.HikariDataSource
sharding.jdbc.datasource.replica-ds-0.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.replica-ds-0.username=one_dev
sharding.jdbc.datasource.replica-ds-0.password=one_dev

sharding.jdbc.datasource.replica-ds-1.jdbc-url=jdbc:mysql://localhost:3306/java_camp_demo_b?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
sharding.jdbc.datasource.replica-ds-1.type=com.zaxxer.hikari.HikariDataSource
sharding.jdbc.datasource.replica-ds-1.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.replica-ds-1.username=one_dev
sharding.jdbc.datasource.replica-ds-1.password=one_dev


sharding.jdbc.config.masterslave.load-balance-algorithm-type=round_robin
sharding.jdbc.config.masterslave.master-data-source-name=primary-ds
sharding.jdbc.config.masterslave.slave-data-source-names=replica-ds-0,replica-ds-1
sharding.jdbc.config.masterslave.name=ms
sharding.jdbc.config.props.sql.show=true


```

service实现如下：

```java

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

```

添加测试类，分别测试只读，事务内只读，写之后读，事务内写后读使用数据源的情况：

```java

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

```

只读结果如下，可看到使用了从库数据源replica-ds-0：

```
2020-12-13 11:33:10.672  INFO 32266 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:33:10.672  INFO 32266 --- [           main] ShardingSphere-SQL                       : SQL: select * from order_item order by id desc limit 10 ::: DataSources: replica-ds-0

```

事务内只读，可看到使用从库数据源replica-ds-0：

```
2020-12-13 11:37:03.029  INFO 32438 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:37:03.029  INFO 32438 --- [           main] ShardingSphere-SQL                       : SQL: select * from order_item order by id desc limit 10 ::: DataSources: replica-ds-0

```

写之后读，可看到写入时使用主库 primary-ds，查询时使用从库replica-ds-0：

```
2020-12-13 11:35:18.960  INFO 32345 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:35:18.960  INFO 32345 --- [           main] ShardingSphere-SQL                       : SQL: INSERT INTO `order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`)  VALUES (?,?,?,?,?) ::: DataSources: primary-ds
2020-12-13 11:35:18.995  INFO 32345 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:35:18.995  INFO 32345 --- [           main] ShardingSphere-SQL                       : SQL: select * from order_item order by id desc limit 10 ::: DataSources: replica-ds-0

```

事务内写后读，写之后读，可看到写入时使用主库 primary-ds，写入后的查询时使用从库replica-ds-0，解决了写完读不一致的问题：

```
2020-12-13 11:27:28.991  INFO 32116 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:27:28.991  INFO 32116 --- [           main] ShardingSphere-SQL                       : SQL: INSERT INTO `order_item`( `order_id`, `commodity_id`, `num`, `origin_price`, `actual_price`)  VALUES (?,?,?,?,?) ::: DataSources: primary-ds
2020-12-13 11:27:29.019  INFO 32116 --- [           main] ShardingSphere-SQL                       : Rule Type: master-slave
2020-12-13 11:27:29.020  INFO 32116 --- [           main] ShardingSphere-SQL                       : SQL: select * from order_item order by id desc limit 10 ::: DataSources: primary-ds

```

