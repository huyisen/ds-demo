package com.github.huyisen.ds.netty.handler;

import com.github.huyisen.ds.netty.common.CommandType;
import com.github.huyisen.ds.netty.common.NettyServer;
import com.github.huyisen.ds.netty.common.Pair;
import com.github.huyisen.ds.netty.processor.NettyRequestProcessor;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author huyisen@corp.netease.com
 * @since 2022-09-09
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final String HEART_BEAT = "HEART_BEAT";

    private final ConcurrentHashMap<CommandType, Pair<NettyRequestProcessor, ExecutorService>> processors = new ConcurrentHashMap<>();
    private final NettyServer nettyServer;

    public NettyServerHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public void registerProcessor(CommandType commandType, NettyRequestProcessor processor) {
        this.processors.putIfAbsent(commandType, new Pair<>(processor, nettyServer.getDefaultExecutor()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (HEART_BEAT.equals(msg)) {
//            System.out.println("服务端：收到客户端心跳请求->" + LocalDateTime.now());
            return;
        }
        CommandType commandType = CommandType.valueOf((String) msg);
        final Pair<NettyRequestProcessor, ExecutorService> pair = processors.get(commandType);
        pair.getRight().submit(() -> pair.getLeft().process(ctx.channel(), commandType));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("服务端：建立链接->" + LocalDateTime.now());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("服务端：关闭链接->" + LocalDateTime.now());
        ctx.channel().close();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelConfig config = ch.config();

        if (!ch.isWritable()) {
            if (logger.isWarnEnabled()) {
                logger.warn("{} is not writable, over high water level : {}",
                        ch, config.getWriteBufferHighWaterMark());
            }
            config.setAutoRead(false);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("{} is writable, to low water : {}",
                        ch, config.getWriteBufferLowWaterMark());
            }
            config.setAutoRead(true);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught : {}", cause.getMessage(), cause);
        ctx.channel().close();
    }
}
