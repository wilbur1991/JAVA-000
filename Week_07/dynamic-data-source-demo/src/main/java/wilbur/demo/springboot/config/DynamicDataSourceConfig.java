/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of springbootDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-10
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package wilbur.demo.springboot.config;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

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
