package com.feishu.blog.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 把所有传入的 String 做 trim，
 * 若结果为空串就转成 null。
 */
@ControllerAdvice
public class GlobalStringTrimBinder {

    @InitBinder
    public void bindStringTrim(WebDataBinder binder) {
        // true = empty string 转 null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
