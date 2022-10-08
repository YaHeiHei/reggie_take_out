package com.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//拦截类上加了RestController与Controller注解的类
@ResponseBody//返回json数据加的注解
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 用来处理添加时名称相同的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)   //声明要拦截异常的类型
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"该昵称已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 用来处理删除时与其它有关联不能删除时自定义抛出的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)   //声明要拦截异常的类型
    public R<String> exceptionHandle(CustomException ex){
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
