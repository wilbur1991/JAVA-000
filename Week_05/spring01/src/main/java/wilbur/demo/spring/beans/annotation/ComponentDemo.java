/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of spring01.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-17
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package wilbur.demo.spring.beans.annotation;


import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
public class ComponentDemo {
    private String name;

    @Override
    public String toString() {
        return new StringJoiner(", ", ComponentDemo.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }
}
