package com.github.huyisen.ds.netty.processor;

import com.github.huyisen.ds.netty.Worker;
import com.github.huyisen.ds.netty.common.CommandType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class TaskDispatchProcessor implements NettyRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskDispatchProcessor.class);

    @Override
    public void process(Channel channel, CommandType command) {
        if (CommandType.TASK_DISPATCH_REQUEST != command) {
            throw new RuntimeException("非当前处理器处理请求！");
        }
        logger.info("-[Worker] 已经收到{}请求，等待分配运行资源运行任务！", command);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        logger.info("-[Worker] 获取到资源，开始运行任务");
        logger.info("-[Worker] 给 Master 发送 TASK_EXECUTE_RUNNING 消息！");
        Worker.getClient().send(CommandType.TASK_EXECUTE_RUNNING);

        logger.info("-[Worker] 任务运行中...");
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        logger.info("-[Worker] 任务执行完了...");

        logger.info("-[Worker] 给 Master 发送 TASK_EXECUTE_RESULT 消息！");
        Worker.getClient().send(CommandType.TASK_EXECUTE_RESULT);
    }
}
