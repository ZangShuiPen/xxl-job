package com.example.xxljobadmin.quartz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * @Author zhangsp
 * @Description 任务模型
 * @Date 2023/12/18
 **/
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobModel {

    private String name;
    private String group;

    private String cronExpression;
    private Class<Job> jobClass;

    public TriggerKey getTriggerkey(){
         TriggerKey triggerKey = new TriggerKey(name, group);
         return triggerKey;
    }

    public JobDetail getJobDeatail(){
       return JobBuilder.newJob(jobClass).withIdentity(name,group).build();
    }

}
