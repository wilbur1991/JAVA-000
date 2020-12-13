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

package wilbur.demo.springboot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wilbur.demo.springboot.annotation.DynamicDataSource;
import wilbur.demo.springboot.config.DynamicDataSourceConfig;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    private String loadBalance() {
        Map<Object, DataSource> dataSourceMap = dynamicDataSourceConfig.getResolvedDataSources();
        List<Object> keys = new ArrayList<>(dataSourceMap.keySet()).stream().collect(Collectors.toList());
        final int i = ThreadLocalRandom.current().nextInt(keys.size());
        return keys.get(i).toString();
    }

}
