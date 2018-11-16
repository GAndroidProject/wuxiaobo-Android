package com.xiaoe.network.utils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/12/1.
 */
public class ThreadPoolProxy {

    private ThreadPoolExecutor executor;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

    public ThreadPoolProxy(int corePoolSize,int maximumPoolSize,long keepAliveTime){
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize =maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public void initThreadPoolProxy(){
        if(executor == null){
            synchronized (ThreadPoolProxy.class){
                if(executor == null){
                    BlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
                    executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,workQueue,threadFactory,handler);
                }
            }
        }
    }

    public void execute(Runnable runnable){
        initThreadPoolProxy();
        executor.execute(runnable);
    }
    public void remove(Runnable runnable){
        initThreadPoolProxy();
        executor.remove(runnable);
    }


}
