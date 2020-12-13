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

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.demo.student")
public class StudentProperties {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
