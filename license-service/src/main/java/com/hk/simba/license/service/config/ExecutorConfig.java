package com.hk.simba.license.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chenm
 * @since 2022/4/21
 */
@Configuration
@EnableAsync
public class ExecutorConfig implements AsyncConfigurer {

    /**
     * 线程池维护的线程核心数
     */
    private int corePoolSize = 10;

    /**
     * 线程池维护的线程最大数
     */
    private int maxPoolSize = 100;

    /**
     * 缓存队列
     */
    private int queueCapacity = 200;

    @Override
    public Executor getAsyncExecutor() {
        return this.asyncExecutor();
    }

    @Bean("asyncExecutor")
    @Primary
    public TaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("asyncExecutor-");
        // 对拒绝task的处理策略
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
