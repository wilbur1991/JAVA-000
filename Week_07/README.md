

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
## 3.（必做）**读写分离 - 数据库框架版本 2.0

最近加班太多，2，3后续补充。