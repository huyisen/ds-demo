package com.github.huyisen.ds.netty;

import com.github.huyisen.ds.netty.common.CommandType;
import com.github.huyisen.ds.netty.common.NettyClient;
import com.github.huyisen.ds.netty.common.NettyServer;
import com.github.huyisen.ds.netty.processor.TaskDispatchProcessor;
import com.github.huyisen.ds.netty.processor.TaskExecuteResultAckProcessor;
import com.github.huyisen.ds.netty.processor.TaskExecuteRunningAckProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private static final int WORKER_SERVER_PORT = 8888;
    private static final String WORKER_CLIENT_HOST = "127.0.0.1";
    private static final int WORKER_CLIENT_PORT = 9999;
    private static NettyClient client;

    public static synchronized NettyClient getClient() {
        if (client == null) {
            try {
                client = new NettyClient(WORKER_CLIENT_HOST, WORKER_CLIENT_PORT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(WORKER_SERVER_PORT);
        server.init();
        server.registerProcessor(CommandType.TASK_DISPATCH_REQUEST, new TaskDispatchProcessor());
        server.registerProcessor(CommandType.TASK_EXECUTE_RUNNING_ACK, new TaskExecuteRunningAckProcessor());
        server.registerProcessor(CommandType.TASK_EXECUTE_RESULT_ACK, new TaskExecuteResultAckProcessor());
        server.start();
    }
}
