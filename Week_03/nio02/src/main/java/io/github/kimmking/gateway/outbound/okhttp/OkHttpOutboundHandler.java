package io.github.kimmking.gateway.outbound.okhttp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkHttpOutboundHandler {
    private String backendUrl;
    private OkHttpClient okHttpClient;

    public OkHttpOutboundHandler() {
        this.okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(60, 10L, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .followRedirects(false)
                .build();
    }

    public void handle(final String backendUrl, final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = backendUrl + fullRequest.uri();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                ctx.close(); //需要关闭否则会阻塞请求
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(fullRequest, ctx, response);
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx, final Response response) {

        FullHttpResponse finalResponse = null;
        try {
            byte[] body = response.body().bytes();
            finalResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            finalResponse.headers().set("Content-Type", "application/json");
            finalResponse.headers().setInt("Content-Length", Integer.parseInt(response.header("Content-Length")));
        } catch (Exception e) {
            e.printStackTrace();
            finalResponse = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    ctx.write(finalResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(finalResponse);
                }
            }
            ctx.flush();
        }
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
