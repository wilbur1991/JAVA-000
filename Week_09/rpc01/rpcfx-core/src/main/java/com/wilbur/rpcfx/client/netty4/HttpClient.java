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


import com.alibaba.fastjson.JSON;
import com.wilbur.rpcfx.api.RpcfxRequest;
import com.wilbur.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpClient}.
 */
public final class HttpClient {

    static final String URL = System.getProperty("url", "http://127.0.0.1:8089/");

    public static RpcfxResponse post(RpcfxRequest rpcfxRequest, String url) throws Exception {
        // Prepare the HTTP request.
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/",
                Unpooled.wrappedBuffer(JSON.toJSONBytes(rpcfxRequest)));
        request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
        post(request, url);
        return new RpcfxResponse();
    }

    public static void post(FullHttpRequest httpRequest, String url) throws Exception {
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new HttpClientInitializer(sslCtx));

            // Make the connection attempt.
            Channel ch = b.connect().sync().channel();

            // Send the HTTP request.
            ch.writeAndFlush(httpRequest);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String requestStr = "{\"method\":\"findById\",\"params\":[1],\"serviceClass\":\"com.wilbur.rpcfx.demo.api.UserService\"}";
        post(JSON.parseObject(requestStr,RpcfxRequest.class),URL);
    }

}
