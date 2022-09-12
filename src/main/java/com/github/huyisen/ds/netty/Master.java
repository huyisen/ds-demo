package com.github.huyisen.ds.netty;

import com.github.huyisen.ds.netty.common.CommandType;
import com.github.huyisen.ds.netty.common.NettyClient;
import com.github.huyisen.ds.netty.common.NettyServer;
import com.github.huyisen.ds.netty.processor.TaskExecuteResponseProcessor;
import com.github.huyisen.ds.netty.processor.TaskExecuteRunningProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Master {

    private static final Logger logger = LoggerFactory.getLogger(Master.class);

    private static final int MASTER_SERVER_PORT = 9999;
    private static final String MASTER_CLIENT_HOST = "127.0.0.1";
    private static final int MASTER_CLIENT_PORT = 8888;
    private static NettyClient client;

    public static synchronized NettyClient getClient() {
        if (client == null) {
            try {
                client = new NettyClient(MASTER_CLIENT_HOST, MASTER_CLIENT_PORT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("-[Master] 开始提交任务给Worker执行！");
        getClient().send(CommandType.TASK_DISPATCH_REQUEST);

        NettyServer server = new NettyServer(MASTER_SERVER_PORT);
        server.init();
        server.registerProcessor(CommandType.TASK_EXECUTE_RUNNING, new TaskExecuteRunningProcessor());
        server.registerProcessor(CommandType.TASK_EXECUTE_RESULT, new TaskExecuteResponseProcessor());
        server.start();
    }
}
