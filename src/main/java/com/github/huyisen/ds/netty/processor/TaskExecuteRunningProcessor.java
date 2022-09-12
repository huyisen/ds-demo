package com.github.huyisen.ds.netty.processor;

import com.github.huyisen.ds.netty.Master;
import com.github.huyisen.ds.netty.common.CommandType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TaskExecuteRunningProcessor implements NettyRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecuteRunningProcessor.class);

    @Override
    public void process(Channel channel, CommandType command) {
        if (CommandType.TASK_EXECUTE_RUNNING != command) {
            throw new RuntimeException("非当前处理器处理请求！");
        }
        logger.info("-[Master] 已经收到{}请求，等待将Task开始执行状态更新到数据库！", command);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        logger.info("-[Master] Task开始执状态更新到数据库成功！");
        logger.info("-[Master] 给 Worker 发送 TASK_EXECUTE_RUNNING_ACK 消息！");
        Master.getClient().send(CommandType.TASK_EXECUTE_RUNNING_ACK);
    }
}
