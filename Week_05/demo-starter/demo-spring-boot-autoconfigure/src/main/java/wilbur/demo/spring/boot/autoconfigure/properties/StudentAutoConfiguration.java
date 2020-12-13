/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of custom-starter.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-19
 * @Author jiangwenbo
 *
 *******************************************************/

package wilbur.demo.spring.boot.autoconfigure.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Student.class)
@EnableConfigurationProperties(StudentProperties.class)
public class StudentAutoConfiguration {
    @Autowired
    private StudentProperties studentProperties;

    @Bean
    @ConditionalOnMissingBean
    public Student student(){
        return new Student(studentProperties.getId(),studentProperties.getName());
    }
}
