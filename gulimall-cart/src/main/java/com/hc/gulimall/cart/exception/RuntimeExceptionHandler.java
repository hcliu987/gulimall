package com.hc.gulimall.cart.exception;

import com.hc.common.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RuntimeExceptionHandler {
    /**
     * 全局统一异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException exception){
        return R.error(exception.getMessage());
    }

    @ExceptionHandler(CartExceptionHandler.class)
    public  R userHandler(CartExceptionHandler exceptionHandler){
        return  R.error("购物车无此商品");
    }
}
