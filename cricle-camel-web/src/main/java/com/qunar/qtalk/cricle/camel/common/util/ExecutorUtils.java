package com.qunar.qtalk.cricle.camel.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author : mingxing.shao
 * Date : 16-4-6
 */
public class ExecutorUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorUtils.class);

    private volatile static ExecutorService executorService;

    private static ExecutorService newLimitedCachedThreadPool(int activeNum, int queueNum) {
        LOGGER.debug("the activeNum is {}, the queueNum is {}", activeNum, queueNum);
        if(executorService == null) {
            synchronized (ExecutorUtils.class) {
                if(executorService == null) {
                    executorService = new ThreadPoolExecutor(activeNum, activeNum, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueNum));
                }
            }
        }
        return executorService;
    }

    public static ExecutorService newLimitedCachedThreadPool() {
        return newLimitedCachedThreadPool(100, 100);
    }
}
