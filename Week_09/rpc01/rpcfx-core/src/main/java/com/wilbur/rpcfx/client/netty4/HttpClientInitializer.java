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

package com.wilbur.rpcfx.client.netty4;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        // Enable HTTPS if necessary.
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
        p.addLast(new HttpResponseDecoder());
        // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
        p.addLast(new HttpRequestEncoder());
        ch.pipeline().addLast(new HttpObjectAggregator(1024*1024));
        ch.pipeline().addLast(new HttpServerExpectContinueHandler());

        p.addLast(new HttpClientHandler());
    }

}
