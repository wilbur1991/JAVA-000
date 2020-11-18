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

package wilbur.demo.springboot.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentConfig {
    @Bean
    public Student student100() {
        return new Student(100, "wilbur100");
    }

    @Bean
    public Student student101() {
        return new Student(101, "wilbur101");
    }
}
