package com.qunar.qtalk.cricle.camel.common.util;

import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;

/**
 * Author : mingxing.shao
 * Date : 16-4-12
 */
public class JsonResultUtils {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResultUtils.class);

    public static JsonResult<?> success() {
        return success(null);
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(JsonResult.SUCCESS, JsonResult.SUCCESS_CODE, "", data);
    }

    public static JsonResult<?> fail(int errcode, String errmsg) {
        return new JsonResult<>(JsonResult.FAIL, errcode, errmsg, "");
    }

    public static <T> JsonResult<T> fail(int errcode, String errmsg, T data) {
        return new JsonResult<>(JsonResult.FAIL, errcode, errmsg, data);
    }

    public static JsonResult<?> fail(BaseCode baseCode) {
        return new JsonResult<>(JsonResult.FAIL, baseCode.getCode(), baseCode.getMsg(), "");
    }

    public static <T> JsonResult<T> fail(BaseCode baseCode,T data) {
        return new JsonResult<>(JsonResult.FAIL, baseCode.getCode(), baseCode.getMsg(), data);
    }

    public static JsonResult<?> failWithArgs(BaseCode baseCode, String... args) {
        return new JsonResult<>(JsonResult.FAIL, baseCode.getCode(), String.format(baseCode.getMsg(), args), "");
    }

    public static JsonResult<?> response(String response) {
        try {
            if (response == null) {
                return new JsonResult<>(JsonResult.FAIL, 1, "parse response fail", "");
            }
            JsonResult result = JacksonUtils.string2Obj(response, JsonResult.class);
            return result;
        } catch  (Exception e) {
            return new JsonResult<>(JsonResult.FAIL, 1, "parse response fail", "");
        }
    }
}
