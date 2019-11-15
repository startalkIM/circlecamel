package com.qunar.qtalk.cricle.camel.common.schedule;

import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.SpringHelper;
import com.qunar.qtalk.cricle.camel.common.exception.TaskException;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.common.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by haoling.wang on 2019/1/9.
 * <p>
 * 新建的定时任务需要继承此类，并且在{@link TaskConfig} 中配置
 */
@Slf4j
public abstract class AbstractTask implements SchedulingConfigurer {

    public static final TaskProperties TASK_PROPERTIES = SpringHelper.getBean(TaskProperties.class);

    public static final Map<String, Map<String, TaskInfo>> TASK_STAT_MAP = new ConcurrentHashMap<>();

    protected String taskName;

    protected String cron;

    protected AtomicInteger status = new AtomicInteger(); // 任务执行状态,0:未开始,1:执行中

    protected ThreadLocal<TaskContext> taskExecuteStat = new ThreadLocal<>();

    public AbstractTask(String taskName, String cron) {
        this.taskName = taskName;
        this.cron = cron;
    }

    public abstract void doTask() throws TaskException;


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(getRunnable(), triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(cron);
            Date nextExec = cronTrigger.nextExecutionTime(triggerContext);
            return nextExec;
        });
    }

    public Runnable getRunnable(){
        return () -> {
            String localIP = IPUtils.getLocalIP();
            String executeIp = TASK_PROPERTIES.getExecuteIp();
            if (StringUtils.equalsIgnoreCase(localIP, executeIp)) {
                try {
                    beforeTask();
                    doTask();
                } catch (TaskException e) {
                    log.error(e.getMessage(), e);
                    failTask();
                } finally {
                    afterTask();
                }
            }
        };
    }

    public void beforeTask() {
        if (status.get() == 1) {
            log.error("task {} is already execute,this execute is skip", taskName);
            return;
        }
        String executeIp = IPUtils.getLocalIP();
        String executeTaskNum = IDUtils.getUUID();
        log.info("taskName [{}] execute start,ip [{}],executeNum [{}]", taskName, executeIp, executeTaskNum);
        status.set(1);
        TaskInfo taskInfo = TaskInfo.builder().executeNum(executeTaskNum)
                .executeIp(executeIp).startDate(new Date()).curExecuteStatus(TaskStatusEnum.EXECUTING).build();
        taskExecuteStat.set(TaskContext.builder().executeNum(executeTaskNum).taskFlag(true).build());

        if (TASK_STAT_MAP.get(taskName) == null) {
            Map<String, TaskInfo> taskInfoMap = Maps.newConcurrentMap();
            TASK_STAT_MAP.put(taskName, taskInfoMap);
        }
        TASK_STAT_MAP.get(taskName).put(executeTaskNum, taskInfo);
    }

    public void failTask() {
        TaskContext taskContext = taskExecuteStat.get();
        taskContext.setTaskFlag(false); // 将此次任务标志为失败
    }

    public void afterTask() {
        String executeTaskNum = taskExecuteStat.get().getExecuteNum();
        Boolean taskFlag = taskExecuteStat.get().getTaskFlag();
        TaskInfo taskInfo = TASK_STAT_MAP.get(taskName).get(executeTaskNum);
        taskInfo.setCurExecuteStatus(TaskStatusEnum.resultOf(taskFlag));
        taskInfo.setEndDate(new Date());
        log.info("taskName [{}] execute finsh,task cost {} ms", taskName, taskInfo.getCost());
        resetTask();
    }

    private void resetTask() {
        status.set(0);
        taskExecuteStat.remove();
    }

    protected void changeCron(String newCron) {
        this.cron = newCron;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getCron() {
        return cron;
    }
}
