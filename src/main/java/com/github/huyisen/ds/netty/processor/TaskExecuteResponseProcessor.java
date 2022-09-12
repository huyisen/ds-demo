package com.github.huyisen.ds.netty.processor;

import com.github.huyisen.ds.netty.Master;
import com.github.huyisen.ds.netty.common.CommandType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TaskExecuteResponseProcessor implements NettyRequestProcessor{

    private static final Logger logger = LoggerFactory.getLogger(TaskExecuteResponseProcessor.class);

    @Override
    public void process(Channel channel, CommandType command) {
        if (CommandType.TASK_EXECUTE_RESULT != command) {
            throw new RuntimeException("非当前处理器处理请求！");
        }
        logger.info("-[Master] 已经收到{}请求，等待将Task执行完成状态更新到数据库！", command);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        logger.info("-[Master] Task执行完成状态更新到数据库成功！");
        logger.info("-[Master] 给 Worker 发送 TASK_EXECUTE_RESULT_ACK 消息！");
        Master.getClient().send(CommandType.TASK_EXECUTE_RESULT_ACK);
    }
}
