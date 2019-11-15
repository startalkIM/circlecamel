package com.qunar.qtalk.cricle.camel.common.schedule;

import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/10.
 * <p>
 * 任务统计对象
 */
@Data
public class TaskStat implements Serializable {

    private String taskName;

    private List<TaskInfo> taskInfoList;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class TaskInfo implements Serializable {

    private String executeNum; //执行编号

    private String executeIp;

    private Date startDate;

    private Date endDate;

    private Long cost;

    private TaskStatusEnum curExecuteStatus; // 任务执行结果状态,0:未开始,1:执行中,2:执行成功,3:执行失败

    public String getStartDate() {
        return DateUtils.convertDateToStr(startDate);
    }

    public String getEndDate() {
        return DateUtils.convertDateToStr(endDate);
    }

    public Long getCost() {
        return endDate.getTime() - startDate.getTime();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TaskContext implements Serializable {

    private String executeNum;

    private Boolean taskFlag = true; // 任务执行的flag,true:成功,false:失败
}

