package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义 定时任务类
 */
@Component
@Slf4j
public class Mytask {
    /**
     * 入门案例，每5秒打印一次日志
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void simpleTest(){
        log.info(LocalDateTime.now().toString());
    }

}
