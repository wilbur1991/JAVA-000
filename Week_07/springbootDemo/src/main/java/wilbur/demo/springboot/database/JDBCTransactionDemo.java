/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-17
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package wilbur.demo.springboot.database;


import java.sql.*;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;

/**
 * 数据库表结构：
 * CREATE TABLE `student` (
 * `id` bigint(20) NOT NULL AUTO_INCREMENT,
 * `name` varchar(255) DEFAULT NULL,
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
public class JDBCTransactionDemo {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/job?serverTimezone=UTC";

    // Database credentials
    static final String USER = "one_dev";
    static final String PASS = "one_dev";
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


    /**
     * 使用PreparedStatement 批量插入
     *
     * @param conn
     * @throws SQLException
     */
    private static void insert(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO student(id,name) " + "VALUES (?,?)");
        preparedStatement.setLong(1, 100);
        preparedStatement.setString(2, "wilbur100");
        preparedStatement.addBatch();
        preparedStatement.setLong(1, 200);
        preparedStatement.setString(2, "wilbur200");
        preparedStatement.addBatch();
        preparedStatement.executeBatch();
    }

}
