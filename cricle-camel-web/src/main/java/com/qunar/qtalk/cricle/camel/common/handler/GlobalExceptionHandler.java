package com.qunar.qtalk.cricle.camel.common.handler;

import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by haoling.wang on 2019/1/2.
 * <p>
 * 全局异常处理器
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public JsonResult defaultExceptionHandler(HttpServletRequest req, Exception e) {
        log.error("global exception handle process...", e);

        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if (e instanceof IllegalArgumentException) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
    }
}
