/*******************************************************
 * Copyright (C) 2020 demo.COM - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-10
 * @Author jiangwenbo <demo>
 *
 *******************************************************/

package wilbur.demo.springboot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import wilbur.demo.springboot.constant.DataSourceConstant;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
        Map<Object, Object> targetDataSources = new HashMap<>(5);
        targetDataSources.put("readA", readADataSource());
        targetDataSources.put("readB", readBDataSource());
        return new DynamicDataSourceConfig(writeDataSource(), targetDataSources);
    }

    @Bean("jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(getRoutingDataSourceConfig());
    }

}
