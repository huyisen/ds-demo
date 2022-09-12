package com.github.huyisen.ds.netty.common;

import com.github.huyisen.ds.netty.handler.NettyServerHandler;
import com.github.huyisen.ds.netty.processor.NettyRequestProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author huyisen@corp.netease.com
 * @since 2022-09-08
 */
public class NettyServer {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private final int port;
    private ServerBootstrap bootstrap;
    private final NettyServerHandler serverHandler = new NettyServerHandler(this);
    private final ExecutorService defaultExecutor = Executors.newFixedThreadPool(2);

    public NettyServer(int port) {
        this.port = port;
    }

    public void registerProcessor(CommandType commandType, NettyRequestProcessor processor) {
        serverHandler.registerProcessor(commandType, processor);
    }

    public void init() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("server-idle-handle", new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                .addLast("decoder", new StringDecoder())
                                .addLast(serverHandler)
                                .addLast("encoder", new StringEncoder())
                        ;
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap = bootstrap;
    }

    public void start() throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        if (channelFuture.isSuccess()) {
//            System.out.println("服务端：启动成功！");
            //等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } else {
            System.err.println("服务端：启动失败！");
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(10086);
        try {
            server.init();
            server.start();
        } finally {
            server.stop();
        }
    }

    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }
}
