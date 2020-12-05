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


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtil {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl( "jdbc:mysql://localhost:3306/job?serverTimezone=UTC" );
        config.setUsername( "one_dev" );
        config.setPassword( "one_dev" );
        config.setMaximumPoolSize(20);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    private DataSourceUtil() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
