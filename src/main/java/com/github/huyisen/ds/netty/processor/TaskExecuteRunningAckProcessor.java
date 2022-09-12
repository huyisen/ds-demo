package com.github.huyisen.ds.netty.processor;

import com.github.huyisen.ds.netty.common.CommandType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecuteRunningAckProcessor implements NettyRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecuteRunningAckProcessor.class);

    @Override
    public void process(Channel channel, CommandType command) {
        if (CommandType.TASK_EXECUTE_RUNNING_ACK != command) {
            throw new RuntimeException("非当前处理器处理请求！");
        }
        logger.info("-[Worker] 收到 Master 的 TASK_EXECUTE_RUNNING_ACK！");
    }
}
