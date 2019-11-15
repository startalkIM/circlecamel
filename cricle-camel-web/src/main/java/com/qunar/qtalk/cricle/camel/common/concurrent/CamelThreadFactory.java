package com.qunar.qtalk.cricle.camel.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by haoling.wang on 2019/1/8.
 */
public class CamelThreadFactory implements ThreadFactory {

    public static final String THREAD_POOL_NAME = "Cricle-Camel-";

    private static final AtomicInteger poolId = new AtomicInteger(); // static

    private final AtomicInteger nextId = new AtomicInteger();

    private final String prefix;

    private final boolean daemon;

    public CamelThreadFactory(String poolName, boolean daemon) {
        this.prefix = THREAD_POOL_NAME + poolName + "-" + poolId.getAndIncrement() + "-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, prefix.concat(String.valueOf(nextId.getAndIncrement())));
        thread.setDaemon(daemon);
        return thread;
    }
}
