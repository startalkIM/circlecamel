package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.SendPush;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.HttpClientUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostManageSearchVo;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.SEND_NOTIFY_FAIL;

/**
 * Created by haoling.wang on 2019/1/21.
 */
@Slf4j
public class CamelPostServiceTest extends BaseTest {

    @Resource
    private CamelPostMapper camelPostMapper;

    @Resource
    private CamelPostService camelPostService;

    @Resource
    private SendPush sendPush;

    private final static String objPattern = "\\[obj type=\"([\\w]+)\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
//    private final static String objPattern = "\\+)?\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);

    //    private final static String objPattern = "\\";

    private String sendUrl = "http://127.0.0.1:8031/innerpackage/ejaapi/qtalk/send_notify";

    public boolean sendNotify(Set<String> user, String data) {
        Dictionary<String, Object> args = new Hashtable<>();
        args.put("from", "admin@ejabhost1");
        args.put("category", "12");
        args.put("data", "test");
        args.put("to", user);
        String res = null;
        try {
            res = HttpClientUtils.postJson(sendUrl, JSON.toJSONString(args));
            log.info("normal send notify to :{}, ret;{}", JSON.toJSONString(args), res);
        } catch (Exception e) {
            log.error("发送通知消息异常,{}", e);
            return false;
        }
        return true;
    }

    @Test
    public void tes() {
        String users = "[\"123@ejabhost1\",\"123@ejabhost1\",\"123@ejabhost1\"]";

        List<String> userList = JSON.parseArray(users, String.class);
        EventModel eventModel = new EventModel();
        eventModel.setToUser("123");
        eventModel.setToHost("ejabhost1");
        eventModel.setEventType(EventType.COMMENT);
        eventModel.setFromUser("123");
        eventModel.setFromHost("ejabhost1");
        Set<String> us = new TreeSet<>();
        us.add("123.li@ejabhost1");
        us.add("123.feng@ejabhost1");
        sendNotify(us, JacksonUtils.obj2String(eventModel));
    }


    @Test
    public void testMapper() {
        //System.out.println(camelPostMapper.selectDeleteFlag("0-2A4564C6E3814573AD546CD5D4D7ED78").equals(0));
        //System.out.println(camelPostService.queryExistsByPostUUID("0-2A4564C6E3814573AD546CD5D4D7ED78"));
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        String time = simpleDateFormat.format(today);
        System.out.println(time);
    }

    @Test
    public void testMap() throws IOException {
        List<Integer> lis = new ArrayList<>();
        lis.add(1);
        lis.add(2);
        lis.add(3);
        lis.add(4);
        lis.add(5);
        lis.add(6);
        List<Integer> newLis = lis.stream().filter(x -> x > 3).collect(Collectors.toList());
        System.out.println("lis size is:" + newLis.size());
        List<String> uuid = new ArrayList<>();
        uuid.add("0-e9fb2ae391b3437d88a2f3f6e440c0d2");
        List<CamelPost> sd = camelPostService.getPostListByUUIDList(uuid);
        String content = sd.get(0).getContent();
        Map<String, Object> AD = JacksonUtils.string2Map(content);
        String contentT = (String)AD.get("content");
        String exContent = (String)AD.get("exContent");
        List<Map<String, String>> fd;
        if(Strings.isNullOrEmpty(exContent)){
            fd = getObjList(contentT);
        }else {
            fd = getObjList(exContent);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map<String,String> mapTemp:fd) {
            stringBuffer.append(mapTemp.get("value"));
        }
        System.out.println(stringBuffer.toString());
    }

//    public String parseContent(String content) {
//        if (Strings.isNullOrEmpty(content)) {
//            return "";
//        }
//        Map<String, Object> AD = JacksonUtils.string2Map(content);
//        String exContent = (String) AD.get("exContent");
//        if(Strings.isNullOrEmpty(exContent)){
//            exContent = (String) AD.get("content");
//        }
//
//    }
    public List<Map<String, String>> getObjList(String srcObj) {
        List<Map<String, String>> result = new ArrayList();
        if (srcObj == null) {
            srcObj = "";
        }
        if (srcObj.length() < 21) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
            return result;
        }

        Matcher m = compiledPattern.matcher(srcObj);
        int start = 0;
        int end = 0;
        while (m.find()) {
            String type = m.group(1);
            String value = m.group(2);
            String ext = null;
            if (m.groupCount() >= 3) {
                ext = m.group(3);
            }
            end = m.start();
            if (end > start) {
                Map<String, String> textMap = new HashMap<String, String>();
                textMap.put("type", "text");
                textMap.put("value", srcObj.substring(start, end));
                result.add(textMap);
            }
            start = m.end();

            Map<String, String> objMap = new HashMap<String, String>();
            objMap.put("type", type);
            objMap.put("value", value);
            if (ext != null) {
                objMap.put("extra", ext);
            }
            result.add(objMap);
        }
        if (start == end) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
        } else if (start > end && start < srcObj.length() - 1) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj.substring(start));
            result.add(textMap);
        }
        return result;
    }

    @Test
    public void testSearch() {
        PostManageSearchVo vo = new PostManageSearchVo();
        vo.setCurPage(1);
        vo.setPageSize(10);
        vo.setPostOwnerName("123");

        JsonResult result = camelPostService.search(vo);
        System.out.println(JSON.toJSONString(result));
    }

}