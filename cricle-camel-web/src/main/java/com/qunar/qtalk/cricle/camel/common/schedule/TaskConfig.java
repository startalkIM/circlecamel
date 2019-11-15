package com.qunar.qtalk.cricle.camel.common.schedule;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.exception.TaskException;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.task.CamelCommentLikeNumSyncTask;
import com.qunar.qtalk.cricle.camel.task.CamelPostLikeNumSyncTask;
import com.qunar.qtalk.cricle.camel.task.SynchroStasticDataTask;
import com.qunar.qtalk.cricle.camel.task.UpdateUsersSyncTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.CronExpression;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/9.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({TaskProperties.class})
public class TaskConfig implements InitializingBean {

    private Map<String, String> taskConfigMap = Maps.newHashMap();

    private TaskProperties taskProperties;

    public TaskConfig(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    @Bean
    @ConditionalOnClass({TaskConfig.class})
    public CamelPostLikeNumSyncTask camelPostLikeNumSyncTask() {
        Pair<String, String> taskInfo = getTaskInfo(CamelPostLikeNumSyncTask.class);
        return new CamelPostLikeNumSyncTask(taskInfo.getLeft(), taskInfo.getRight());
    }

    @Bean
    @ConditionalOnClass({TaskConfig.class})
    public CamelCommentLikeNumSyncTask camelCommentLikeNumSyncTask() {
        Pair<String, String> taskInfo = getTaskInfo(CamelCommentLikeNumSyncTask.class);
        return new CamelCommentLikeNumSyncTask(taskInfo.getLeft(), taskInfo.getRight());
    }

    @Bean
    @ConditionalOnClass({TaskConfig.class})
    public UpdateUsersSyncTask updateUsersSyncTask() {
        Pair<String, String> taskInfo = getTaskInfo(UpdateUsersSyncTask.class);
        return new UpdateUsersSyncTask(taskInfo.getLeft(), taskInfo.getRight());
    }

    @Bean
    @ConditionalOnClass({TaskConfig.class})
    public SynchroStasticDataTask synchroStasticDataTask() {
        Pair<String, String> taskInfo = getTaskInfo(SynchroStasticDataTask.class);
        return new SynchroStasticDataTask(taskInfo.getLeft(), taskInfo.getRight());
    }

    private Pair<String, String> getTaskInfo(Class clazz) {
        Assert.assertArgNotNull(clazz, "获取任务配置参数为空");

        if (MapUtils.isEmpty(taskConfigMap)) {
            throw new TaskException("schedule task not config,please check");
        }
        String simpleName = clazz.getSimpleName();
        if (!taskConfigMap.containsKey(simpleName)) {
            throw new TaskException(String.format("task name:%s is not config,please check", simpleName));
        }
        String cron = taskConfigMap.get(simpleName);
        return Pair.of(simpleName, cron);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> taskMap = taskProperties.getTaskMap();
        if (MapUtils.isEmpty(taskMap)) {
            throw new TaskException("schedule task not config,please check");
        }
        taskMap.forEach((taskName, cron) -> {
            if (Strings.isNullOrEmpty(cron) || !cron.startsWith("(") || !cron.endsWith(")")) {
                throw new TaskException(String.format("task %s config cron string not match (* * * * * *) layout"));
            }
            cron = cron.substring(1, cron.length() - 1);
            if (!CronExpression.isValidExpression(cron)) {
                throw new TaskException(String.format("task name:%s config cron is illegal,curent config cron is %s", taskName, cron));
            }
            log.info("task: {} => {}", taskName, cron);
            taskConfigMap.put(taskName, cron);
        });
    }

}
