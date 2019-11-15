package com.qunar.qtalk.cricle.camel.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.dto.UserModelDto;
import com.qunar.qtalk.cricle.camel.common.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.SEND_NOTIFY_FAIL;

/**
 * SendPush
 *
 * @author binz.zhang
 * @date 2019/1/14
 */
@Component
@Slf4j
public class SendPush {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendPush.class);
    private static final String FROM = "admin@ejabhost1";

    @Value("${url_send_notify}")
    private String sendUrl;

    @Value("${qtalk_send_message}")
    private String sendMessageUrl;

    public boolean sendNotify(UserModelDto user, String data) {
        Dictionary<String, Object> args = new Hashtable<>();
        args.put("from", FROM);
        args.put("category", "12");
        args.put("data", data);
        args.put("to", user.getBareJid());
        String res = null;
        try {
            res = HttpClientUtils.postJson(sendUrl, JSON.toJSONString(args));
            log.info("normal send notify to :{}, ret;{}", JSON.toJSONString(args), res);
        } catch (Exception e) {
            log.error("发送通知消息异常,{}", e);
            return false;
        }
        if (!checkPushResult(res)) {
            return false;
        }
        return true;
    }
    public boolean bathSendNotify(List<String> users, String data) {
        Dictionary<String, Object> args = new Hashtable<>();
        args.put("from", FROM);
        args.put("category", "12");
        args.put("data", data);
        args.put("to", users);
        String res = null;
        try {
            res = HttpClientUtils.postJson(sendUrl, JSON.toJSONString(args));
            log.info("normal send notify to :{}, ret;{}", JSON.toJSONString(args), res);
        } catch (Exception e) {
            log.error("发送通知消息异常,{}", e);
            return false;
        }
        if (!checkPushResult(res)) {
            return false;
        }
        return true;
    }

    public boolean sendMessage(String msg) {
        String res;
        try {
            res = HttpClientUtils.postJson(sendMessageUrl, msg);
            LOGGER.info("send message to :{}, ret;{}", msg, res);
        } catch (Exception e) {
            LOGGER.error("send message fail", e);
            return false;
        }
        return true;
    }

    public boolean sendNotifyForPost(UserModelDto user, String data) {
        Dictionary<String, Object> args = new Hashtable<>();
        args.put("from", FROM);
        args.put("category", "13");
        args.put("data", data);
        args.put("to", user.getBareJid());
        String res = null;
        try {
            res = HttpClientUtils.postJson(sendUrl, JSON.toJSONString(args));
            LOGGER.info(" post send notify to :{}, ret;{}", JSON.toJSONString(args), res);
        } catch (Exception e) {
            LOGGER.error("发送通知消息异常,{}", e);
            return false;
        }
        return checkPushResult(res);
    }

    public Map<UserModelDto, Boolean> sendNotify(List<UserModelDto> users, String data) {
        Dictionary<String, Object> args = new Hashtable<>();
        Map<UserModelDto, Boolean> pushResMap = Maps.newHashMap();
        args.put("from", FROM);
        args.put("category", "12");
        args.put("data", data);
        for (UserModelDto user : users) {
            args.put("to", user.getBareJid());
            String ret = null;
            try {
                ret = HttpClientUtils.postJson(sendUrl, JSON.toJSONString(args));
                LOGGER.info("send notify to :{}, ret;{}", JSON.toJSONString(args), ret);
            } catch (Exception e) {
                LOGGER.error("发送通知消息异常,{}", e);
            }
            pushResMap.put(user, checkPushResult(ret));
        }
        return pushResMap;
    }

    /**
     * sendPush 返回结果校验
     *
     * @param result true:成功
     * @return
     */
    private boolean checkPushResult(String result) {
        boolean resStatus = false;
        try {
            if (!Strings.isNullOrEmpty(result)) {
                JSONObject receivedParam = JSON.parseObject(result);
                resStatus = (Boolean) receivedParam.get("ret");
            }
        } catch (Exception e) {
            LOGGER.error("sendPush 返回结果解析异常", e);
        }
        return resStatus;
    }
}
