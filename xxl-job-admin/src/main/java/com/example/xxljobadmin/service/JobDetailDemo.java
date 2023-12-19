package com.example.xxljobadmin.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author zhangsp
 * @Description
 * @Date 2023/12/19
 **/
@Slf4j
public class JobDetailDemo implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info(String.format(">>>>>>>>>>>>>>>>>>>>>静态化"));
    }
}
