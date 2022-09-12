package com.github.huyisen.ds.netty.handler;

import com.github.huyisen.ds.netty.common.CommandType;
import com.github.huyisen.ds.netty.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huyisen@corp.netease.com
 * @since 2022-09-09
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final String HEART_BEAT = "HEART_BEAT";

    private final ConcurrentHashMap<CommandType, NettyRequestProcessor> processors = new ConcurrentHashMap<>();

    public void registerProcessor(CommandType commandType, NettyRequestProcessor processor) {
        this.processors.putIfAbsent(commandType, processor);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (HEART_BEAT.equals(msg)) {
//            System.out.println("服务端：收到客户端心跳请求->" + LocalDateTime.now());
            return;
        }
        CommandType commandType = CommandType.valueOf((String) msg);
        processors.get(commandType).process(ctx.channel(), commandType);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("客户端：建立链接->" + LocalDateTime.now());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("客户端：关闭链接->" + LocalDateTime.now());
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
//            System.out.println("客户端：发送心跳请求->" + LocalDateTime.now());
//            ctx.channel().writeAndFlush(HEART_BEAT)
//                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
