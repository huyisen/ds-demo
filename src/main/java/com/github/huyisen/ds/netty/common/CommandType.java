package com.github.huyisen.ds.netty.common;

public enum CommandType {
    /**
     * dispatch task request
     */
    TASK_DISPATCH_REQUEST,

    /**
     * task execute running, from worker to master
     */
    TASK_EXECUTE_RUNNING,

    /**
     * task execute running ack, from master to worker
     */
    TASK_EXECUTE_RUNNING_ACK,

    /**
     * task execute response, from worker to master
     */
    TASK_EXECUTE_RESULT,

    /**
     * task execute response ack, from master to worker
     */
    TASK_EXECUTE_RESULT_ACK,

    TASK_KILL_REQUEST,

    TASK_KILL_RESPONSE,

    /**
     * HEART_BEAT
     */
    HEART_BEAT,

}
