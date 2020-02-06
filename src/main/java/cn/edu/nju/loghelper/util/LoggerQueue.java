package cn.edu.nju.loghelper.util;

import cn.edu.nju.loghelper.entity.LoggerMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LoggerQueue {
    private static final int QUEUE_MAX_SIZE = 10000;
    private static LoggerQueue loggerQueue = new LoggerQueue();

    private BlockingQueue<LoggerMessage> blockingQueue = new LinkedBlockingDeque<>(QUEUE_MAX_SIZE);

    private LoggerQueue(){}

    public static LoggerQueue getInstance() {
        return loggerQueue;
    }

    public boolean push(LoggerMessage message) {
        return this.blockingQueue.add(message);
    }

    public LoggerMessage poll() {
        LoggerMessage message = null;
        try {
            message = this.blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }
}
