package com.olo.ding.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {


    public static int cores = Runtime.getRuntime().availableProcessors();

    public static ExecutorService SocketTransThreadPool;

    static {
        //0.8为阻塞系数
        SocketTransThreadPool = new ThreadPoolExecutor(
                cores, (int)(cores / (1 - 0.8)), 10L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SocketTransThreadPool.shutdown();
            try {
                if (!SocketTransThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    SocketTransThreadPool.shutdownNow();
                    // 等待任务取消的响应
                    if (!SocketTransThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    }
                }
            } catch (Exception e) {
            }
        }));
        
    }
    public static void main(String[] args) {
    	    Calendar calendar = Calendar.getInstance();
    	    Date date = new Date(System.currentTimeMillis());
    	    calendar.setTime(date);
    	    calendar.add(Calendar.DAY_OF_MONTH, +7);//+1今天的时间加一天
    	    date = calendar.getTime();
    	    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
    	    System.out.println(formatter.format(date));
    	    
	}
    
}
