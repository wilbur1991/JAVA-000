/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of demo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-10-28
 * @Author jiangwenbo
 *
 *******************************************************/

package wilbur.demo;


import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientDemo {
    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(60, 10L, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .followRedirects(false)
                .build();
    }

    public static void main(String[] args) {
        try {
            System.out.println(request("http://localhost:8801"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String request(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        String body = "";
        try {
            response = okHttpClient.newCall(request).execute();
            body =response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("request error",e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return body;
    }
}
