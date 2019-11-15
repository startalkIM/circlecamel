package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.CamelNotifyFlagEnum;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.NotifyConfigQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelNotifyConfig;
import com.qunar.qtalk.cricle.camel.mapper.CamelNotifyConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class CamelNotifyConfigService {

    public static final int NOTIFY_CONFIG_DEFAULT_VERSION = 1;

    public static final String CAMEL_NOTIFY_CONFIG_KEY = "camel.notify.config.key";

    @Resource
    private CamelNotifyConfigMapper camelNotifyConfigMapper;

    /**
     * 获取用户通知配置
     * @param notifyConfigQueryVo
     * @return
     */
    public JsonResult getNotifyConfigByUserInfo(NotifyConfigQueryVo notifyConfigQueryVo) {
        Assert.assertArgNotNull(notifyConfigQueryVo, "notifyConfigQueryVo is not allow null");
        CamelNotifyConfig camelNotifyConfig = camelNotifyConfigMapper.queryNotifyConfigByUserInfo(notifyConfigQueryVo);
        if (null == camelNotifyConfig) {
            // 新增一条初始化记录
            camelNotifyConfig = CamelNotifyConfig.builder()
                    .notifyUser(notifyConfigQueryVo.getNotifyUser()).host(notifyConfigQueryVo.getHost())
                    .flag(CamelNotifyFlagEnum.ON.flag).updateVersion(NOTIFY_CONFIG_DEFAULT_VERSION)
                    .notifyKey(CAMEL_NOTIFY_CONFIG_KEY).build();
            camelNotifyConfigMapper.insertSelective(camelNotifyConfig);
        }
        return JsonResultUtils.success(camelNotifyConfig);
    }

    /**
     * 更新用户通知配置
     * @param notifyConfigQueryVo
     * @return
     */
    public JsonResult updateNotifyConfig(NotifyConfigQueryVo notifyConfigQueryVo) {
        Assert.assertArgNotNull(notifyConfigQueryVo, "notifyConfigQueryVo is not allow null");
        int i = camelNotifyConfigMapper.updateByUserInfo(notifyConfigQueryVo);
        if (Objects.equals(i, 1)) {
            log.info("user [{}] change notify config to [{}]", notifyConfigQueryVo.getNotifyUser(),
                    CamelNotifyFlagEnum.flagOf(notifyConfigQueryVo.getFlag()));
            // todo 配置更新成功，同步其他系统

            return JsonResultUtils.success(camelNotifyConfigMapper.queryNotifyConfigByUserInfo(notifyConfigQueryVo));
        }
        return JsonResultUtils.fail(BaseCode.DB_ERROR);
    }
}
