package com.sicau.tiredsys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhong  on 2019/6/9 18:57
 */
@Configuration
public class ThreadPoolConfig {

        @Bean(name ="threadPool")
        public ThreadPoolTaskExecutor taskAsyncPool() {

            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(4);

            executor.setMaxPoolSize(8);

            executor.setQueueCapacity(100);

            executor.setKeepAliveSeconds(60);

            executor.setThreadNamePrefix("Pool-Async");

            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

            executor.initialize();

            return executor;

        }
}
