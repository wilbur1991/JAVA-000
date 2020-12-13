/*******************************************************
 * Copyright (C) 2020  - All Rights Reserved
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

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = getConn();
            conn.setAutoCommit(false);
            System.out.println("Connected database successfully...");

            deleteAll(conn);
            insert(conn);
            System.out.println("select all after insert");
            select(conn);
            update(conn, 100L, "wilburNewName");
            System.out.println("select all after update");
            select(conn);
            //测试事务提交情况
//            if(Objects.nonNull(conn)) {
//                throw new SQLException("throw random exception");
//            }
            conn.commit();
            System.out.println("select all after transaction commit");
            select(conn);
            //事务提交后，ID为100的记录不会被删除
            delete(conn, 100L);

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
