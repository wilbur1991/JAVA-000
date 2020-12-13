/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-17
 * @Author jiangwenbo <demo>
 *
 *******************************************************/

package wilbur.demo.springboot.database;


import java.sql.*;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class HikariCPDemo {
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

    private static void update(Connection conn, Long id, String newName) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("update student set name = ? where id = ?");
        preparedStatement.setString(1, newName);
        preparedStatement.setLong(2, id);
        preparedStatement.execute();
    }

    private static void deleteAll(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("delete from student where id >0");
        preparedStatement.execute();
    }

    private static void delete(Connection conn, Long id) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("delete from student where id =?");
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
    }

    private static void select(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("select * from student ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            System.out.println(String.format("student id:%d name:%s", id, name));
        }
    }
}
