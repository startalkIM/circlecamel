package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.dto.CamelRetainModel;
import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelRetainMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelStatisticMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CamelStatisticDataService
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
@Service
@Slf4j
public class CamelStatisticDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelStatisticDataService.class);
    @Resource
    private CamelPostMapper camelPostMapper;
    @Resource
    private CamelCommentMapper camelCommentMapper;
    @Resource
    private CamelStatisticMapper camelStatisticMapper;

    @Resource
    private CamelRetainMapper camelRetainMapper;

    private static final String ACTIVE_KEY = "activeUserNum";
    private static final String VALID_ACTIVE_KEY = "vaildActiveUserNum";
    private static final String BROWS_TIME_KEY = "browsTime:";
    public static final List<String> BROWS_TIME_DIVISION = Arrays.asList(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
            "22", "23"});


    @Resource
    private RedisUtil redisUtil;

    public List<CamelPost> getPostsByDate(Date c) {
        return camelPostMapper.getPostListByTime(c);
    }

    public List<CamelComment> getCommentsByDate(Date c) {
        return camelCommentMapper.getCommentListByTime(c);
    }

    public boolean recordActiveUser(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            RedisAccessor.execute(s -> redisUtil.sadd(s, userId), ACTIVE_KEY);
        }
        return true;
    }

    public boolean recordValidActiveUser(String userId) {
        if (!Strings.isNullOrEmpty(userId)) {
            RedisAccessor.execute(s -> redisUtil.sadd(s, userId), VALID_ACTIVE_KEY);
        }
        return true;
    }

    public boolean recordBrowsUser(String timeKey, String userId) {
        if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(timeKey)) {
            RedisAccessor.execute(s -> redisUtil.sadd(s, userId), BROWS_TIME_KEY.concat(timeKey));
        }
        return true;
    }

    public long getActiveUserNum() {
        long active = RedisAccessor.execute(s -> redisUtil.scard(s), ACTIVE_KEY);
        RedisAccessor.execute(s -> redisUtil.del(s), ACTIVE_KEY);
        return active;
    }

    public Set<String> getActiveUserId() {
        Set<String> userID = RedisAccessor.execute(s -> redisUtil.smembers(s), ACTIVE_KEY);
        return userID;
    }

    public long getValidActiveUserNum() {
        long active = RedisAccessor.execute(s -> redisUtil.scard(s), VALID_ACTIVE_KEY);
        RedisAccessor.execute(s -> redisUtil.del(s), VALID_ACTIVE_KEY);
        return active;
    }

    public HashMap<String, String> getBrowsTimeUserNum() {
        HashMap<String, String> browsTimeUser = new HashMap<>(24);
        for (String time : BROWS_TIME_DIVISION) {
            if (RedisAccessor.execute(s -> redisUtil.exists(s), BROWS_TIME_KEY.concat(time))) {
                long num = RedisAccessor.execute(s -> redisUtil.scard(s), BROWS_TIME_KEY.concat(time));
                browsTimeUser.put(time, String.valueOf(num));
                RedisAccessor.execute(s -> redisUtil.del(s), BROWS_TIME_KEY.concat(time));
            } else {
                browsTimeUser.put(time, String.valueOf(0));
            }
        }

        return browsTimeUser;
    }

    public CamelStatisicDto getDataByTime(Date d) {
        return camelStatisticMapper.selectCSTByTime(d);
    }

    public List<CamelStatisicDto> getDataByTimeScorp(Date start, Date end) {
        return camelStatisticMapper.selectCSTByTimeScorp(start, end);
    }


    public List<CamelRetainModel> getCamelRetainDataByTimeScorp(Date start, Date end){
        return camelRetainMapper.selectCamelRetain(start,end);
    }







    public int insertDateCSD(CamelStatisicDto camelStatisicDto) {
        return camelStatisticMapper.insertCamelStatisic(camelStatisicDto);
    }

    public LinkedHashMap<String, Integer> buildEmptyBrowse() {
        LinkedHashMap<String, Integer> emptyTree = new LinkedHashMap<>();
        BROWS_TIME_DIVISION.stream().forEach(x -> {
            emptyTree.put(x, 0);
        });
        return emptyTree;
    }

    public List<String> getIntervalDate(String start, String end) throws ParseException {
        java.util.Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(start);//定义起始日期
        java.util.Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(end);//定义结束日期
        List<String> dateList = new ArrayList<>();
        if (start.equals(end)) {
            dateList.add(start);
            return dateList;
        }
        if (d1.after(d2)) {
            log.info("get the interval date fail due to the start after end");
            throw new IllegalArgumentException("get the interval date fail due to the start after end");
        }
        Calendar dd = Calendar.getInstance();//定义日期实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        dd.setTime(d1);//设置日期起始时间
        while (dd.getTime().before(d2)) {//判断是否到结束日期
            String str = sdf.format(dd.getTime());
            dateList.add(str);
            dd.add(Calendar.DATE, 1);//进行当前日期月份加1
        }
        dateList.add(sdf.format(d2.getTime()));
        return dateList;

    }


    public void camelRetainService(Timestamp time) {
        log.info("计算有效用户的留存>>>>");
        CamelRetainModel camelRetainModel = new CamelRetainModel();
        camelRetainModel.setCreateTime(time);
        Set<String> curBrow = getActiveUserId();
        if (curBrow == null) {
            return;
        }
        camelRetainModel.buildEmptyRetainRate();
        String dataArry[] = curBrow.toArray(new String[0]);
        if (dataArry == null) {
            dataArry = new String[1];
            camelRetainModel.setBrowUser(dataArry);
            camelRetainMapper.insertCamelRetainModel(camelRetainModel);
            return;
        }
        camelRetainModel.setBrowUser(dataArry);
        camelRetainMapper.insertCamelRetainModel(camelRetainModel);
        log.info("insert into camelRetainModel to camel_retain_table");
        calculateRetain(camelRetainModel);
    }

    public void calculateRetain(CamelRetainModel camelRetainModel) {

        if (camelRetainModel == null || camelRetainModel.getBrowUser() == null) {
            return;
        }
        DecimalFormat df = new DecimalFormat( "0.00 ");
        Set<String> browseUserCur = new HashSet<>(Arrays.asList(camelRetainModel.getBrowUser()));
        for (Integer i : ContextConsts.REATAIN_DATE) {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date(camelRetainModel.getCreateTime().getTime()));
            ca.add(Calendar.DATE, -i);
            CamelRetainModel camelRetainModel1 = camelRetainMapper.selectCamelRetainByTime(new Date(ca.getTime().getTime()));
            if (camelRetainModel1 == null || camelRetainModel1.getBrowUser() == null || camelRetainModel1.getBrowUser().length == 0) {
                continue;
            }
            Set<String> browseUserBefo = new HashSet<String>(Arrays.asList(camelRetainModel1.getBrowUser()));
            Sets.SetView<String> intersection = Sets.intersection(browseUserBefo, browseUserCur);


            double reatin_i = ((double)intersection.size() / (browseUserBefo.size()))*100; //计算百分比
            //reatin_i = Double.parseDouble(df.format(reatin_i));
            JSONObject O = JSONObject.parseObject(camelRetainModel1.getRetainRate());
            O.put(String.valueOf(i),String.valueOf(reatin_i));
            camelRetainModel1.setRetainRate(O.toJSONString());
            log.info("curTime is {},update beforeTime {}, key is {} retain is {}",camelRetainModel.getCreateTime().getTime(),ca.getTime().getTime(),i,reatin_i);
            camelRetainMapper.updateReatinRateById(O.toJSONString(),camelRetainModel1.getId());
        }


    }


    public HashMap<String,String> getRetainRate(CamelRetainModel camelRetainModel){
        String retainData = camelRetainModel.getRetainRate();
        if(camelRetainModel==null || retainData==null || retainData.equals("")){
            return CamelRetainModel.buildEmptyRetainRateHashMap();
        }
        HashMap<String,String> ad = JacksonUtils.string2Obj(retainData,HashMap.class);
        for (Integer i:ContextConsts.REATAIN_DATE) {
            if(!ad.containsKey(String.valueOf(i))){
                ad.put(String.valueOf(i),"0.0");
            }
        }
        return ad;
    }


    public HashMap<String,Integer> getRetainNum(CamelRetainModel camelRetainModel){
        String retainData = camelRetainModel.getRetainRate();
        if(camelRetainModel==null || retainData==null || retainData.equals("")){
            return CamelRetainModel.buildEmptyRetainNumHashMap();
        }
        Set<String> browseUserCur = new HashSet<>(Arrays.asList(camelRetainModel.getBrowUser()));
        HashMap<String,Integer> reatin_i = new HashMap<>();
        for (Integer i : ContextConsts.REATAIN_DATE) {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date(camelRetainModel.getCreateTime().getTime()));
            ca.add(Calendar.DATE, -i);
            CamelRetainModel camelRetainModel1 = camelRetainMapper.selectCamelRetainByTime(new Date(ca.getTime().getTime()));
            if (camelRetainModel1 == null || camelRetainModel1.getBrowUser() == null) {
                reatin_i.put(String.valueOf(i),0);
                continue;
            }
            Set<String> browseUserBefo = new HashSet<String>(Arrays.asList(camelRetainModel1.getBrowUser()));
            Sets.SetView<String> intersection = Sets.intersection(browseUserBefo, browseUserCur);
            if(intersection!=null){
                reatin_i.put(String.valueOf(i),intersection.size());
            }else {
                reatin_i.put(String.valueOf(i),0);

            }
        }
        return reatin_i;
    }




    public void insertCamelRetain(CamelRetainModel camelRetainModel) {
        camelRetainMapper.insertCamelRetainModel(camelRetainModel);
    }


    public static void main(String[] args) {
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        map.put("00", 1);
        map.put("01", 2);
        map.put("10", 2);
        map.put("11", 1);
        map.put("20", 3);
        map.put("21", 4);
        map.put("22", 5);

        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }


}
