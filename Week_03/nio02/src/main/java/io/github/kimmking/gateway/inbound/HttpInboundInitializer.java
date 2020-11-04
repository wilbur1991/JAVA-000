package io.github.kimmking.gateway.inbound;

import io.github.kimmking.gateway.filter.HttpRequestCustomHeaderFilter;
import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.Arrays;
import java.util.List;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

	private List<String> proxyServers;

	public HttpInboundInitializer(List<String> proxyServers) {
		this.proxyServers = proxyServers;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
//		if (sslCtx != null) {
//			p.addLast(sslCtx.newHandler(ch.alloc()));
//		}
		p.addLast(new HttpServerCodec());
		//p.addLast(new HttpServerExpectContinueHandler());
		p.addLast(new HttpObjectAggregator(1024 * 1024));
		List<HttpRequestFilter>filterList = Arrays.asList(new HttpRequestCustomHeaderFilter());
		p.addLast(new HttpInboundHandler(this.proxyServers,filterList));
	}
}
