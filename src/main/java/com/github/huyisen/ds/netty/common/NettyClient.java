package com.github.huyisen.ds.netty.common;

import com.github.huyisen.ds.netty.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huyisen@corp.netease.com
 * @since 2022-09-08
 */
public class NettyClient {

    private final String host;
    private final int port;
    private Bootstrap bootstrap;
    private Channel channel;

    public NettyClient(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;

        init();
        start();
    }

    private void init() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyClient_%d", this.threadIndex.incrementAndGet()));
            }
        });
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("server-idle-handle", new IdleStateHandler(0, 29, 0, TimeUnit.SECONDS))
                                .addLast(new StringDecoder(), new NettyClientHandler(), new StringEncoder());
                    }
                });

        this.bootstrap = bootstrap;
    }

    private void start() throws InterruptedException {
        ChannelFuture future = bootstrap.connect(host, port).sync();
        if (future.isSuccess()) {
//            System.out.println("客户端：启动成功！");
            this.channel = future.channel();
        } else {
            throw new RuntimeException("客户端：启动失败！");
        }
    }

    public void send(CommandType command) {
        channel.writeAndFlush(command.toString());
    }
}
