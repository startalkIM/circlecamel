package com.qunar.qtalk.cricle.camel.common.concurrent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = ThreadPoolConfig.THREAD_POOL_CONFIG_PREFIX)
public class ThreadPoolConfig {

    public static final String THREAD_POOL_CONFIG_PREFIX = "thread.pool";

    /**
     * Set the ThreadPoolExecutor's core pool size.
     */
    private int corePoolSize;
    /**
     * Set the ThreadPoolExecutor's maximum pool size.
     */
    private int maxPoolSize;
    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     */
    private int queueCapacity;

    @Bean("threadPoolExecutor")
    public Executor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadFactory(new CamelThreadFactory("common-pool",true));
        executor.setRejectedExecutionHandler(new DiscardAndLogPolicy());
        executor.initialize();
        log.info("asyncTaskExecutor init finally");
        return executor;
    }

    class DiscardAndLogPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.error("impossible. task " + r + " discarded from " + executor.toString());
            // add for main-thread run
            r.run();
        }
    }

}
