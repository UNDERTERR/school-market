package com.xiaojie.product.exception;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;



@Slf4j
@RestControllerAdvice(basePackages = "io.niceseason.gulimall.product.controller")
public class MarketExceptionAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handlerValidException(MethodArgumentNotValidException exception) {
        Map<String, String> map = new HashMap<>();
        // 1. 获取校验结果
        BindingResult result = exception.getBindingResult();
        // 2. 遍历所有字段错误
        result.getFieldErrors().forEach(item -> {
            String message = item.getDefaultMessage();  // 错误信息
            String field = item.getField();            // 字段名
            map.put(field, message);                   // 封装错误信息
        });

        // 3. 记录日志
        log.error("数据校验出现问题:{},异常类型{}",
                exception.getMessage(), exception.getClass());

        // 4. 返回统一错误响应
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(),
                        BizCodeEnum.VAILD_EXCEPTION.getMsg())
                .put("data", map);
    }
}
