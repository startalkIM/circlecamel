package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.dto.CamelRetainModel;
import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.CamelStatisicFlowModel;
import com.qunar.qtalk.cricle.camel.common.vo.CamelStatisicUserAction;
import com.qunar.qtalk.cricle.camel.service.CamelStatisticDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CamelStatQueryController
 *
 * @author binz.zhang
 * @date 2019/2/14
 */
@Controller
@Slf4j
@RequestMapping("/cricle_camel/")
public class CamelStatQueryController {
    @Resource
    private CamelStatisticDataService camelStatisticDataService;

    @RequestMapping(value = "/newapi/getData")
    public String login(Model model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                        @RequestParam(value = "Date", required = false) Date data) throws IOException {
        CamelStatisicDto camelStatisicDto = camelStatisticDataService.getDataByTime(data);
        HashMap<String, String> brows = (HashMap<String, String>) JSONObject.parseObject(camelStatisicDto.getBrowsTimeUser(), Map.class);
        model.addAttribute("data", camelStatisicDto);
        model.addAttribute("day", data.toString());
        model.addAttribute("browse", brows);
        return "resultDisplay";
    }

    @RequestMapping(value = "/newapi/mqttclient")
    public String mqttclient() {
        return "mqttclient";
    }


    @PostMapping(value = "/nck/getCamelData")
    @ResponseBody
    public JsonResult getData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                              @RequestParam(value = "Date", required = false) Date data) throws IOException {
        log.info("get the camel data the param is {}", data);
        Date cur = new Date(System.currentTimeMillis());
        if (data != null && data.after(cur)) {
            log.info("查询时间错误");
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        CamelStatisicDto camelStatisicDto = camelStatisticDataService.getDataByTime(data);
        return JsonResultUtils.success(camelStatisicDto);
    }

    @PostMapping(value = "/nck/getReatinlData")
    @ResponseBody
    public JsonResult getRetainData(@RequestBody String param) throws IOException, ParseException {
        JSONObject receivedParam = JSON.parseObject(param);
        log.info("get other data in the param is{}", param);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = (String) receivedParam.get("beginTime");
        String end = (String) receivedParam.get("endTime");
        String type = (String) receivedParam.get("type");

        if (Strings.isNullOrEmpty(start) || Strings.isNullOrEmpty(end)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);
        if (startDate.after(endDate)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        List<String> intervalDate = camelStatisticDataService.getIntervalDate(start, end);
        List<CamelRetainModel> datas = camelStatisticDataService.getCamelRetainDataByTimeScorp(startDate, endDate);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        HashMap<String, CamelRetainModel> dataHash = new HashMap<>();
        datas.stream().forEach(x -> {
            String dateString = sdf.format(x.getCreateTime());
            dataHash.put(dateString, x);
        });
        LinkedList<HashMap<String, String>> reatinRate = new LinkedList<>();
        LinkedList<HashMap<String, Integer>> reatinNum = new LinkedList<>();

        intervalDate.stream().forEach(x -> {
            HashMap<String,Integer> df = new HashMap<>();
            HashMap<String,String> ef = new HashMap<>();
            if (!dataHash.containsKey(x)) {
                df.put("num",0);
                ef.put("num","0");
                df.putAll(CamelRetainModel.buildEmptyRetainNumHashMap());
                ef.putAll(CamelRetainModel.buildEmptyRetainRateHashMap());
            } else {
                df.put("num",dataHash.get(x).getBrowUser().length);
                ef.put("num",String.valueOf(dataHash.get(x).getBrowUser().length));

                ef.putAll(camelStatisticDataService.getRetainRate(dataHash.get(x)));
                df.putAll(camelStatisticDataService.getRetainNum(dataHash.get(x)));
            }
            reatinRate.add(ef);
            reatinNum.add(df);

        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", intervalDate);
        if (type == null || type.equalsIgnoreCase("rate")) {
            jsonObject.put("retain", reatinRate);
        } else {
            jsonObject.put("retain", reatinNum);
        }
        return JsonResultUtils.success(jsonObject);
    }


    @PostMapping(value = "/nck/getFlowData")
    @ResponseBody
    public JsonResult getDataFlow(@RequestBody String param) throws ParseException {
        log.info("get getFlowData in param:{} ", param);
        JSONObject receivedParam = JSON.parseObject(param);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = (String) receivedParam.get("beginTime");
        String end = (String) receivedParam.get("endTime");
        if (Strings.isNullOrEmpty(start) || Strings.isNullOrEmpty(end)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);
        if (startDate.after(endDate)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        List<String> intervalDate = camelStatisticDataService.getIntervalDate(start, end);
        if (intervalDate == null || intervalDate.size() == 0 || intervalDate.size() > 60) {
            log.info("interval date illegal :{}", JacksonUtils.obj2String(intervalDate));
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        List<CamelStatisicDto> datas = camelStatisticDataService.getDataByTimeScorp(startDate, endDate);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        HashMap<String, CamelStatisicFlowModel> returnModelHashMap = new HashMap<>();
        datas.stream().forEach(x -> {
            CamelStatisicFlowModel rs = new CamelStatisicFlowModel();
            String dateString = sdf.format(x.getDataTime());
            LinkedHashMap<String, Integer> acs = new LinkedHashMap<>(24);
            rs.setActive(x.getActiveNum());
            TreeMap<String, String> browse = JSON.parseObject(x.getBrowsTimeUser(), TreeMap.class);
            TreeMap<String, Integer> actiu = new TreeMap<String, Integer>();
            HashMap<String, Integer> acu = new HashMap<>(24);
            CamelStatisticDataService.BROWS_TIME_DIVISION.stream().forEach(xx -> {
                acs.put(xx, Integer.valueOf(browse.get(xx)));
            });
            browse.keySet().stream().forEach(xx -> {
                actiu.put(xx, Integer.valueOf(browse.get(xx)));
            });
            rs.setBrowseUser(acs);
            returnModelHashMap.put(dateString, rs);

        });
        List<Integer> act = new ArrayList<>(intervalDate.size());
        List<LinkedHashMap<String, Integer>> browseUssr = new ArrayList<>(intervalDate.size());
        intervalDate.stream().forEach(x -> {
            HashMap<String, Integer> acu = new HashMap<>(24);
            if (!returnModelHashMap.containsKey(x)) {
                CamelStatisicFlowModel rs = new CamelStatisicFlowModel();
                act.add(0);
                browseUssr.add(camelStatisticDataService.buildEmptyBrowse());
            } else {
                act.add(returnModelHashMap.get(x).getActive());
                browseUssr.add(returnModelHashMap.get(x).getBrowseUser());
            }
        });
        LinkedHashMap<String, Object> re = new LinkedHashMap<>(2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activeUser", act);
        jsonObject.put("browse", browseUssr);
        re.put("activeUser", act);
        re.put("browse", browseUssr);
        return JsonResultUtils.success(re);
    }

    @PostMapping(value = "/nck/getOther")
    @ResponseBody
    public JsonResult getOtherData(@RequestBody String param) throws ParseException {
        JSONObject receivedParam = JSON.parseObject(param);
        log.info("get other data in the param is{}", param);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = (String) receivedParam.get("beginTime");
        String end = (String) receivedParam.get("endTime");
        if (Strings.isNullOrEmpty(start) || Strings.isNullOrEmpty(end)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);
        if (startDate.after(endDate)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        List<String> intervalDate = camelStatisticDataService.getIntervalDate(start, end);
        if (intervalDate == null || intervalDate.size() == 0 || intervalDate.size() > 60) {
            log.info("interval date illegal :{}", JacksonUtils.obj2String(intervalDate));
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        List<CamelStatisicDto> datas = camelStatisticDataService.getDataByTimeScorp(startDate, endDate);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        LinkedHashMap<String, CamelStatisicUserAction> userActionHashMap = new LinkedHashMap<>();
        datas.stream().forEach(x -> {
            CamelStatisicUserAction rs = new CamelStatisicUserAction();
            String dateString = sdf.format(x.getDataTime());
            rs.setDate(dateString);
            rs.setActive(x.getValidActiveNum());
            rs.setPostNum(x.getPostTotalNum());
            rs.setCommentNum(x.getCommentNum());
            rs.setLikeNum(x.getLikeNum());
            rs.setRealNamePost(x.getPostRealnameNum());
            rs.setAnonymousPost(x.getPostAnonymousNum());
            rs.setRealNameComment(x.getCommentRealnameNum());
            rs.setAnonymouComment(x.getCommentAnonymousNum());
            rs.setTopCommentPostUUID(x.getTopCommentPostuuid());
            rs.setTopLikePostUUID(x.getTopLikePostuuid());
            userActionHashMap.put(dateString, rs);
        });
        intervalDate.stream().forEach(x -> {
            if (!userActionHashMap.containsKey(x)) {
                CamelStatisicUserAction rs = new CamelStatisicUserAction();
                userActionHashMap.put(x, rs);
            }
        });
        return JsonResultUtils.success(userActionHashMap.values());

    }


}





