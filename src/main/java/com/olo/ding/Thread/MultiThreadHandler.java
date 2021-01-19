package com.olo.ding.Thread;

public interface MultiThreadHandler {
    /**
     * 添加任务
     * @param tasks 
     */
    void addTask(Runnable... tasks);
    /**
     * 执行任务
     * @throws ChildThreadException 
     */
    void run() throws ChildThreadException;
}