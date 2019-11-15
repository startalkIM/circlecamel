package com.qunar.qtalk.cricle.camel.task;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.qunar.qtalk.cricle.camel.common.dto.CamelSpecialUserDto;
import com.qunar.qtalk.cricle.camel.common.exception.TaskException;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.schedule.AbstractTask;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import com.qunar.qtalk.cricle.camel.mapper.CamelAuthMapper;
import com.qunar.qtalk.cricle.camel.service.CamelAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * UpdateUsersSyncTask
 *
 * @author binz.zhang
 * @date 2019/1/21
 */
@Slf4j
public class UpdateUsersSyncTask extends AbstractTask {
    @Resource
    private CamelAuthMapper camelAuthMapper;

    @Resource
    private CamelAuthService camelAuthService;



    @Resource
    private RedisUtil redisUtil;

    @Value("${redis_user_list}")
    private String redisUserKey;

    public UpdateUsersSyncTask(String taskName, String cron) {
        super(taskName, cron);
    }

    @Override
    public void doTask() throws TaskException {

        long start = System.currentTimeMillis();
        log.info("UpdateUsersSyncTask start!");
        List<CamelUserModel> users = null;
        try {
            users = camelAuthMapper.selectLegalUser();
            users.stream().forEach(x->{
                if(x.getUserHost().equals("1")){
                    x.setUserHost("ejabhost1");
                }
            });
        } catch (RuntimeException e) {
            log.error("update the users into sedis fail,{}", e);

        }
        final List<CamelUserModel> userList = users;

        //把特殊用户也放进redis，否则出现push不到现象
        CamelSpecialUserDto camelSpecialUserDto = null;
        try {
            camelSpecialUserDto = camelAuthService.genQtalkConfig("special.json");
            if(camelSpecialUserDto!=null){
                camelSpecialUserDto.getData().stream().forEach(x->{
                    userList.add(new CamelUserModel(x.getUserID(),"ejabhost1"));
                });
            }
        } catch (IOException e) {
            log.error("read the special.json fail",e);
        }
        //移除key,及时更新set集合
        RedisAccessor.execute(key -> redisUtil.del(key), redisUserKey);
        userList.stream().forEach(x->{
            RedisAccessor.execute(key -> redisUtil.sadd(key, x.toString()), redisUserKey);
        });
        long end = System.currentTimeMillis();
        log.info("UpdateUsersSyncTask finish,cost {} milliseconds!", (end - start));
    }


}
