package com.github.huyisen.ds.netty.processor;

import com.github.huyisen.ds.netty.common.CommandType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecuteResultAckProcessor implements NettyRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecuteResultAckProcessor.class);

    @Override
    public void process(Channel channel, CommandType command) {
        if (CommandType.TASK_EXECUTE_RESULT_ACK != command) {
            throw new RuntimeException("非当前处理器处理请求！");
        }
        logger.info("-[Worker] 收到 Master 的 TASK_EXECUTE_RESULT_ACK！");
    }
}
