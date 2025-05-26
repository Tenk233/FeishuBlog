package com.feishu.blog.service;

public interface ContentService {

    void checkContent(String content, Integer blogId, Integer userId);

    void updateConfig(Integer contentLevel, Integer imageLevel);
}
