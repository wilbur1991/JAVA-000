/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of rpcfx.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-20
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package com.wilbur.rpcfx.client;


import com.wilbur.rpcfx.api.Filter;
import com.wilbur.rpcfx.exception.RpcfxException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

public class RpcfxByteBuddy {
    /**
     * 泛型方法，使用ByteBuddy字节码增强生成代理类实例
     *
     * @param serviceClass
     * @param url
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        try {
            return (T) new ByteBuddy()
                    .subclass(Object.class)
                    .implement(serviceClass)
                    .intercept(InvocationHandlerAdapter.of(new Rpcfx.RpcfxInvocationHandler(serviceClass, url, filters)))
                    .make()
                    .load(RpcfxByteBuddy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcfxException(e.getMessage(), e);
        }
    }
}
