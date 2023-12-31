package com.example.xxljobadmin;

import com.example.xxljobadmin.quartz.DynamicSchedulerUtil;
import com.example.xxljobadmin.quartz.JobModel;
import com.example.xxljobadmin.service.JobDetailDemo;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class XxlJobAdminApplicationTests {

    @Test
    void contextLoads() throws SchedulerException, InterruptedException {
        String triggerKeyName = "test";
        String cronExpression = "0/3 * * * * ? *";
        Class<JobDetailDemo> jobDetailDemoClass = JobDetailDemo.class;
        Map<String, Object> jobDataMap = new HashMap<>();
        jobDataMap.put("id", "1");
        jobDataMap.put("name", "lisi");
        DynamicSchedulerUtil.addJob(triggerKeyName, cronExpression, jobDetailDemoClass, jobDataMap);
        DynamicSchedulerUtil.getScheduler().start();
        List<Map<String, Object>> jobList = DynamicSchedulerUtil.getJobList();
        System.out.println("jobList:"+jobList);
        Thread.sleep(30000);
//        DynamicSchedulerUtil.rescheduleJob(triggerKeyName,"0/5 * * * * ? *");
//        Thread.sleep(400000);
       // DynamicSchedulerUtil.pauseJob(triggerKeyName);


    }

}
