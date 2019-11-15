package com.qunar.qtalk.cricle.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.qunar.qtalk.cricle.camel.common.dto.CamelSpecialUserDto;
import com.qunar.qtalk.cricle.camel.common.dto.ValidateMacTokenResult;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import com.qunar.qtalk.cricle.camel.mapper.CamelAuthMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CamelAuthService
 *
 * @author binz.zhang
 * @date 2019/1/14
 */
@Service
public class CamelAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelAuthMapper.class);

    @Resource
    private RedisUtil redisUtil;

    @Value("${legal_user_type}")
    private String LEGAL_HIRE_TYPE;

    @Value("${camel_circle_switch}")
    private String CAMEL_CIRCLE_SWITCH;

    @Value("${special_users}")
    private String specialUsers;

    @Value("${special_dep1}")
    private String specialDep1;

    @Value("${unlegal_dep1}")
    private String unlegaldep1;


    private static final List<String> users = new ArrayList<>();
    private static final List<String> legalType = new LinkedList<>();
    private static final Set<String> specialUsersForEntrance = new HashSet<>();
    private static final List<String> unleagal_dep = new LinkedList<>();
    private static final Map<String, List<String>> specialDep1ForEntranceMap = new HashMap<>();

    @PostConstruct
    private void init() throws IOException {
        legalType.addAll(Splitter.on(";").splitToList(LEGAL_HIRE_TYPE));
        unleagal_dep.addAll(Splitter.on(";").splitToList(unlegaldep1));
        users.addAll(Splitter.on(";").splitToList(specialUsers));
        CamelSpecialUserDto camelSpecialUserDto = genQtalkConfig("special.json");
        camelSpecialUserDto.getData().stream().forEach(x -> {
            specialUsersForEntrance.add(x.getUserID());
        });
        List<String> specinalDep1List
                = Splitter.on(";").splitToList(specialDep1);
        if (CollectionUtils.isNotEmpty(specinalDep1List)) {
            specinalDep1List.forEach(specinalDep1 -> {
                if (!Strings.isNullOrEmpty(specinalDep1)) {
                    String[] split = StringUtils.split(specinalDep1, ":");
                    String dep1 = split[0];
                    String hireTypeString = split[1];
                    List<String> hireTypeList = Splitter.on(",").splitToList(hireTypeString);
                    specialDep1ForEntranceMap.put(dep1, hireTypeList);
                }
            });
        }
    }

    @Resource
    private CamelAuthMapper camelAuthMapper;

    public boolean authUser(String userId, Integer hostId) {
        String hireType = "", dep1 = "";
        if (!CAMEL_CIRCLE_SWITCH.equals("true")) {
            if (users.contains(userId)) {
                return true;  //特殊用户一直打开接口
            }
            return false;
        }
        try {
            CamelUserModel camelUserModel = camelAuthMapper.selectUserModel(userId, hostId);
            if (camelUserModel != null) {
                hireType = camelUserModel.getHireType();
                dep1 = camelUserModel.getDep1();
            }
        } catch (RuntimeException e) {
            LOGGER.error("select hireType from host_users fail,userId:{},hostId:{}", userId, hostId, e);
            return false;
        }
        if (legalType.contains(hireType) && !unleagal_dep.contains(dep1)) {
            return true;
        }
        if (specialUsersForEntrance.contains(userId)) {
            return true;
        }
        if (specialDep1ForEntranceMap.containsKey(dep1)) {
            List<String> hireTypeList = specialDep1ForEntranceMap.get(dep1);
            if (CollectionUtils.isNotEmpty(hireTypeList)) {
                if (!hireTypeList.contains(hireType)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public CamelSpecialUserDto genQtalkConfig(String configName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(configName);
        InputStream read = classPathResource.getInputStream();
        String config = new String(ByteStreams.toByteArray(read));
        ObjectMapper mapper = new ObjectMapper();
        CamelSpecialUserDto params = mapper.readValue(config, CamelSpecialUserDto.class);
        return params;
    }

    public ValidateMacTokenResult checkMacUserToken(Map<String, String> shareCKey) throws UnsupportedEncodingException {
        ValidateMacTokenResult checkPushKeyResult = ValidateMacTokenResult.builder()
                .validate(true).validateMsg("验证成功").build();
        String u = shareCKey.get("u");
        String k = shareCKey.get("k");
        String t = shareCKey.get("t");
        if (Strings.isNullOrEmpty(u) || Strings.isNullOrEmpty(k) || Strings.isNullOrEmpty(t) || u.indexOf("@") == -1) {
            checkPushKeyResult.setValidate(false);
            checkPushKeyResult.setValidateMsg("参数缺失");
            return checkPushKeyResult;
        }
        String redisKey = u + "_tkey";
        LOGGER.info("redis key:" + redisKey);
        Set<String> fields = redisUtil.hkeys(2, redisKey);
        if (CollectionUtils.isEmpty(fields)) {
            checkPushKeyResult.setValidate(false);
            checkPushKeyResult.setValidateMsg("待发送方未登陆");
            return checkPushKeyResult;
        }
        String strFields = fields.stream().collect(Collectors.joining(","));
        Base64 base64 = new Base64();
        LOGGER.info("fields:" + strFields);
        for (String field : fields) {
            String v = DigestUtils.md5DigestAsHex(field.concat(t).getBytes("UTF-8"));//md5Hex(field.concat(count));
            byte[] bs = v.getBytes("UTF-8");
            String base64Result = new String(bs);
            LOGGER.info("base64 encode: " + base64Result);
            if (k.equals(base64Result)) {
                checkPushKeyResult.setValidate(true);
                checkPushKeyResult.setValidateMsg("登陆验证成功");
                return checkPushKeyResult;
            }
        }
        checkPushKeyResult.setValidate(false);
        checkPushKeyResult.setValidateMsg("接收方登陆验证失败");
        return checkPushKeyResult;
    }

    List<CamelUserModel> getUserByUserName(String userName) {
        if (Strings.isNullOrEmpty(userName)) {
            return null;
        }
        return camelAuthMapper.getUserByUserName(userName);
    }
}
