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
import java.util.Random;
import java.util.StringJoiner;


public class JDBCDemo {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/job?serverTimezone=UTC";

    // Database credentials
    static final String USER = "one_dev";
    static final String PASS = "one_dev";

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
            int step = 100000,total = 1000000;

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

}
