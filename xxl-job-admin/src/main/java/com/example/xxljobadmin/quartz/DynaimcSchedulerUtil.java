package com.example.xxljobadmin.quartz;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author zhangsp
 * @Description 动态的调度器
 * @Date 2023/12/18
 **/
@Slf4j
@Component
public class DynaimcSchedulerUtil implements InitializingBean {

    private Scheduler scheduler;

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler(){
        return this.scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ObjectUtils.isEmpty(scheduler)){
            this.scheduler = new StdSchedulerFactory().getScheduler();
            log.info(String.format(">>>>>>>>>>>调度器schedler是:%s",scheduler));
        }
    }


    // 返回这个调度器运行的jobkey
    public Set<JobKey> getJobKeys() throws SchedulerException {
        try {
            if (!ObjectUtils.isEmpty(scheduler)){
                List<String> jobGroupNames = scheduler.getJobGroupNames();
                String groupName=jobGroupNames.get(0);
                return  scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public boolean addJob(JobModel jobModel) throws SchedulerException {
        if (scheduler.checkExists(jobModel.getTriggerkey())){
             Trigger trigger = scheduler.getTrigger(jobModel.getTriggerkey());
             log.info(String.format(">>>>>>>>>>> trigger+[%s]",trigger));
             return false;
        }
         CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobModel.getCronExpression());
         CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobModel.getTriggerkey()).withSchedule(cronScheduleBuilder).build();
         Date date = scheduler.scheduleJob(jobModel.getJobDeatail(), cronTrigger);
         log.info(String.format(">>>>>>>>>>>> data is %s",date));
         log.info(String.format(">>>>>>>>>>  start job cronTrigger is ：%s , jobDeatail is :%s",cronTrigger,jobModel.getJobDeatail()));
         return true;
    }


}
