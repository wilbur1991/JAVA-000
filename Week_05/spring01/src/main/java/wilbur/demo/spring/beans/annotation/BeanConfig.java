/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of spring01.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-17
 * @Author jiangwenbo
 *
 *******************************************************/

package wilbur.demo.spring.beans.annotation;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wilbur.demo.spring.spring01.Student;

@Configuration
public class BeanConfig {
    @Bean("studentUsingBean")
    public Student newStudent(){
        Student student = new Student();
        student.setId(111);
        student.setName("wilbur111");
        return student;
    }
}
