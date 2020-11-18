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

/**
 * 数据库表结构：
 * CREATE TABLE `student` (
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *   `name` varchar(255) DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
public class JDBCDemo {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/job?serverTimezone=UTC";

    // Database credentials
    static final String USER = "one_dev";
    static final String PASS = "one_dev";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            stmt = conn.createStatement();
            delete(stmt, 100L);
            insert(stmt);
            select(stmt);
            update(stmt, 100L, "wilburNewName");
            select(stmt);
            delete(stmt,100L);

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
        }
    }

    private static void insert(Statement statement) throws SQLException {
        String sql = "INSERT INTO student(id,name) " + "VALUES (100, 'wilbur100')";
        statement.executeUpdate(sql);
    }

    private static void update(Statement statement, Long id, String newName) throws SQLException {
        String sql = "update student set name = '" + newName + "' " + " where id = " + id;
        statement.executeUpdate(sql);
    }

    private static void delete(Statement statement, Long id) throws SQLException {
        String sql = "delete from student where id = " + id;
        statement.executeUpdate(sql);
    }

    private static void select(Statement statement) throws SQLException {
        String sql = "select * from student ";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            System.out.println(String.format("student id:%d name:%s",id,name));
        }
    }
}
