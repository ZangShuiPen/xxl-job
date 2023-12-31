package com.example.xxljobadmin.quartz;

import java.util.*;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;


@Component
public final class DynamicSchedulerUtil implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSchedulerUtil.class);

    // Scheduler
    private static Scheduler scheduler;

    public static void setScheduler(Scheduler scheduler) {
        DynamicSchedulerUtil.scheduler = scheduler;
    }

    public static Scheduler getScheduler() {
       return DynamicSchedulerUtil.scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ObjectUtils.isEmpty(scheduler)){
            DynamicSchedulerUtil.scheduler= StdSchedulerFactory.getDefaultScheduler();
        }
        Assert.notNull(scheduler, "quartz scheduler is null");
        logger.info(">>>>>>>>> init quartz scheduler success.[{}]", scheduler);
    }

    public static List<Map<String,Object>> getJobList(){
         List<Map<String, Object>> jobList = new ArrayList<>();
        try {
            String groupName = scheduler.getJobGroupNames().get(0);
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
            if (jobKeys != null && jobKeys.size()>0){
                for (JobKey jobKey:jobKeys){
                     TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), Scheduler.DEFAULT_GROUP);
                     Trigger trigger = scheduler.getTrigger(triggerKey);
                     JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                     HashMap<String, Object> map = new HashMap<>();
                     map.put("triggerKey",triggerKey);
                     map.put("trigger",trigger);
                     map.put("jobDetail",jobDetail);
                    jobList.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return jobList;
    }
    // addJob 新增
    public static boolean addJob(String triggerKeyName, String cronExpression, Class<? extends Job> jobClass, Map<String, Object> jobData) throws SchedulerException {
        // TriggerKey : name + group
        String group = Scheduler.DEFAULT_GROUP;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyName, group);

        // TriggerKey valid if_exists
        if (scheduler.checkExists(triggerKey)) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            logger.info(">>>>>>>>> Already exist trigger [" + trigger + "] by key [" + triggerKey + "] in Scheduler");
            return false;
        }

        // CronTrigger : TriggerKey + cronExpression
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // JobDetail : jobClass
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(triggerKeyName, group).build();
        if (jobData != null && jobData.size() > 0) {
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.putAll(jobData);    // JobExecutionContext context.getMergedJobDataMap().get("mailGuid");
        }

        // schedule : jobDetail + cronTrigger
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        logger.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
        return true;
    }



    // unscheduleJob 删除
    public static boolean removeJob(String triggerKeyName) throws SchedulerException {
        // TriggerKey : name + group
        String group = Scheduler.DEFAULT_GROUP;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyName, group);

        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            result = scheduler.unscheduleJob(triggerKey);
        }
        logger.info(">>>>>>>>>>> removeJob, triggerKey:{}, result [{}]", triggerKey, result);
        return result;
    }

    // reschedule 重置cron
    public static boolean rescheduleJob(String triggerKeyName, String cronExpression) throws SchedulerException {
        // TriggerKey : name + group
        String group = Scheduler.DEFAULT_GROUP;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyName, group);

        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            Date date = scheduler.rescheduleJob(triggerKey, cronTrigger);
            result = true;
            logger.info(">>>>>>>>>>> resumeJob success, triggerKey:{}, cronExpression:{}, date:{}", triggerKey, cronExpression, date);
        } else {
            logger.info(">>>>>>>>>>> resumeJob fail, triggerKey:{}, cronExpression:{}", triggerKey, cronExpression);
        }
        return result;
    }

    // Pause 暂停
    public static boolean pauseJob(String triggerKeyName) throws SchedulerException {
        // TriggerKey : name + group
        String group = Scheduler.DEFAULT_GROUP;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyName, group);

        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            scheduler.pauseTrigger(triggerKey);
            result = true;
            logger.info(">>>>>>>>>>> pauseJob success, triggerKey:{}", triggerKey);
        } else {
            logger.info(">>>>>>>>>>> pauseJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

    // resume 重启
    public static boolean resumeTrigger(String triggerKeyName) throws SchedulerException {
        // TriggerKey : name + group
        String group = Scheduler.DEFAULT_GROUP;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyName, group);

        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            scheduler.resumeTrigger(triggerKey);
            result = true;
            logger.info(">>>>>>>>>>> resumeJob success, triggerKey:{}", triggerKey);
        } else {
            logger.info(">>>>>>>>>>> resumeJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

}