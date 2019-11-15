package com.qunar.qtalk.cricle.camel.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.SpringHelper;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.schedule.AbstractTask;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.task.CamelCommentLikeNumSyncTask;
import com.qunar.qtalk.cricle.camel.task.CamelPostLikeNumSyncTask;
import com.qunar.qtalk.cricle.camel.task.SynchroStasticDataTask;
import com.qunar.qtalk.cricle.camel.task.UpdateUsersSyncTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/10.
 */
@Slf4j
@RestController
@RequestMapping("newapi/cricle_camel/task")
public class TaskController {

    @Resource
    private CamelCommentLikeNumSyncTask camelCommentLikeNumSyncTask;

    @Resource
    private CamelPostLikeNumSyncTask camelPostLikeNumSyncTask;

    @Resource
    private UpdateUsersSyncTask updateUsersSyncTask;

    @Resource
    private SynchroStasticDataTask synchroStasticDataTask;

    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;

    private final ApplicationContext applicationContext = SpringHelper.getApplicationContext();

    @GetMapping("statResult")
    public JsonResult taskStat() {
        return JsonResultUtils.success(AbstractTask.TASK_STAT_MAP);
    }

    @GetMapping("doCamelCommentLikeNumSyncTask")
    public JsonResult doCamelCommentLikeNumSyncTask() {
        return doTask(camelCommentLikeNumSyncTask);
    }

    @GetMapping("doCamelPostLikeNumSyncTask")
    public JsonResult doCamelPostLikeNumSyncTask() {
        return doTask(camelPostLikeNumSyncTask);
    }

    @GetMapping("doUpdateUsersSyncTask")
    public JsonResult doUpdateUsersSyncTask() {
        return doTask(updateUsersSyncTask);
    }

    @GetMapping("doUpdateStatisicTask")
    public JsonResult doGetStatisic() {
         synchroStasticDataTask.doTask();
         return null;
    }
    @GetMapping("getTaskInfo")
    public JsonResult getTaskInfo() {
        Map<String, AbstractTask> taskMap = applicationContext.getBeansOfType(AbstractTask.class);

        if (MapUtils.isNotEmpty(taskMap)) {
            ArrayList<Map> taskInfoList = Lists.newArrayList();
            taskMap.forEach((k, v) -> {
                Map<String, String> taskInfo = Maps.newHashMap();
                taskInfo.put("taskName", v.getTaskName());
                taskInfo.put("cron", v.getCron());
                taskInfoList.add(taskInfo);
            });
            return JsonResultUtils.success(taskInfoList);
        }
        return JsonResultUtils.success("current system don't have task");
    }

    private JsonResult doTask(AbstractTask abstractTask) {
        try {
            threadPoolExecutor.submit(abstractTask.getRunnable());
            return JsonResultUtils.success(BaseCode.OK);
        } catch (Exception e) {
            log.error("task:{} 触发执行失败", abstractTask.getTaskName(), e);
        }
        return JsonResultUtils.fail(BaseCode.ERROR.getCode(),
                String.format("task:%s 触发执行失败", abstractTask.getTaskName()));
    }
}
