package io.github.kimmking.gateway;


import io.github.kimmking.gateway.inbound.HttpInboundServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {
        String proxyServers = System.getProperty("proxyServer","http://localhost:8801;http://localhost:8802");
        String proxyPort = System.getProperty("proxyPort","8888");

          //  http://localhost:8888/api/hello  ==> gateway API
          //  http://localhost:8088/api/hello  ==> backend service

        int port = Integer.parseInt(proxyPort);
        List<String> proxyServerList = Stream.of(proxyServers.split(";")).collect(Collectors.toList());
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServerList);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServers);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
